from fastapi import FastAPI, File, UploadFile, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field
from typing import List, Optional
import tempfile
import shutil
import os
import re
import fitz
from docx import Document
from dotenv import load_dotenv

from langchain_openai import ChatOpenAI
from langchain_core.prompts import ChatPromptTemplate

# Load environment variables from AI-Recommendation-System-Integration/.env
package_dir = os.path.dirname(__file__)
dotenv_path = os.path.join(package_dir, ".env")
if os.path.exists(dotenv_path):
    load_dotenv(dotenv_path)
else:
    load_dotenv()

OPENAI_API_KEY = os.environ.get("OPENAI_API_KEY")

app = FastAPI(
    title="AI Resume Analysis Service",
    description="Microservice for resume parsing and skill extraction using LangChain AI",
    version="1.0.0",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

class AIResumeAnalysis(BaseModel):
    skills: List[str] = Field(description="Danh sách các kỹ năng chuyên môn và kỹ năng mềm của ứng viên")
    summary: str = Field(description="Tóm tắt ngắn gọn CV trong khoảng 3-4 câu")

class ResumeAnalysisResponse(BaseModel):
    fileName: str
    text: str
    skills: List[str]
    summary: Optional[str] = None

COMMON_SKILLS = [
    "Java", "Spring", "Spring Boot", "Hibernate", "SQL", "MySQL", "PostgreSQL",
    "REST", "APIs", "Docker", "Kubernetes", "AWS", "Azure", "Git", "JavaScript",
    "React", "Node.js", "Python", "Django", "Flask", "HTML", "CSS", "TypeScript",
    "Communication", "Leadership", "Problem Solving", "Teamwork", "Project Management"
]

def normalize_text(text: str) -> str:
    return re.sub(r"\s+", " ", text.replace("\r", " ").replace("\n", " ")).strip()


def extract_text_from_pdf(path: str) -> str:
    document = fitz.open(path)
    pages = [page.get_text() for page in document]
    document.close()
    return "\n".join(pages)


def extract_text_from_docx(path: str) -> str:
    document = Document(path)
    content = [paragraph.text for paragraph in document.paragraphs]
    return "\n".join(content)


def extract_skills_from_text(text: str) -> List[str]:
    found = []
    low = text.lower()
    for skill in COMMON_SKILLS:
        if skill.lower() in low and skill not in found:
            found.append(skill)
    if not found:
        tokens = re.findall(r"\b[a-zA-Z0-9#+\.\-]{2,}\b", text)
        unique = []
        for token in tokens:
            token_norm = token.strip(". ,;")
            if token_norm and token_norm.lower() not in unique:
                unique.append(token_norm.lower())
            if len(unique) >= 10:
                break
        found = [token.capitalize() for token in unique]
    return found[:15]


def summarize_text(text: str) -> str:
    sentences = re.split(r"(?<=[.!?])\s+", text)
    summary = " ".join(sentences[:2]).strip()
    if not summary:
        summary = text[:240].strip()
    return summary or "Tóm tắt ngắn gọn về ứng viên sẽ được cung cấp khi có đầy đủ nội dung CV."


def analyze_resume_with_ai(text: str) -> AIResumeAnalysis:
    api_key = os.environ.get("OPENAI_API_KEY")
    if not api_key:
        print("OPENAI_API_KEY not set. Falling back to local text extraction.")
        return AIResumeAnalysis(skills=extract_skills_from_text(text), summary=summarize_text(text))

    try:
        llm = ChatOpenAI(model_name="gpt-4o-mini", openai_api_key=api_key, temperature=0)
        structured_llm = llm.with_structured_output(AIResumeAnalysis)

        prompt = ChatPromptTemplate.from_messages([
            ("system", "Bạn là một chuyên gia tuyển dụng nhân sự (HR). Nhiệm vụ của bạn là phân tích văn bản CV được cung cấp, trích xuất tất cả các kỹ năng của ứng viên (cả kỹ năng cứng và mềm) và viết một đoạn tóm tắt ngắn gọn, chuyên nghiệp về ứng viên đó bằng tiếng Việt., nếu không có, không trả về gì cả."),
            ("human", "Đây là nội dung CV:\n\n{text}")
        ])

        chain = prompt | structured_llm
        result = chain.invoke({"text": text})
        return result
    except Exception as e:
        print("AI model call failed, using fallback extraction:", str(e))
        return AIResumeAnalysis(skills=extract_skills_from_text(text), summary=summarize_text(text))

@app.post("/resume/analyze", response_model=ResumeAnalysisResponse)
async def analyze_resume(file: UploadFile = File(...)):
    if file is None or file.filename is None:
        raise HTTPException(status_code=400, detail="Resume file is required")

    filename = file.filename
    extension = os.path.splitext(filename)[1].lower()
    if extension not in {".pdf", ".docx"}:
        raise HTTPException(status_code=400, detail="Only PDF and DOCX files are supported")

    with tempfile.NamedTemporaryFile(delete=False, suffix=extension) as tmp:
        shutil.copyfileobj(file.file, tmp)
        tmp_path = tmp.name

    try:
        if extension == ".pdf":
            text = extract_text_from_pdf(tmp_path)
        else:
            text = extract_text_from_docx(tmp_path)

        text = normalize_text(text)
        
        # Gọi LangChain / AI để phân tích CV thay cho Regex
        try:
            ai_result = analyze_resume_with_ai(text)
            skills = ai_result.skills
            summary = ai_result.summary
        except Exception as e:
            raise HTTPException(status_code=500, detail=f"AI processing failed: {str(e)}")

        return ResumeAnalysisResponse(
            fileName=filename,
            text=text,
            skills=skills,
            summary=summary,
        )
    finally:
        try:
            os.remove(tmp_path)
        except Exception:
            pass
