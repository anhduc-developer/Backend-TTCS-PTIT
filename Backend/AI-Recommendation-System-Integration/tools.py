from typing import Optional
from langchain.tools import tool
from data_loader import write_cover_letter_to_doc


@tool("job_search_tool", description="Search jobs based on keywords, title, location, or company.")
def get_job_search_tool(query: str) -> str:
    return f"[Job Search Placeholder] Search requested for: {query}"


def ResumeExtractorTool(resume_text: str):
    @tool(
        "resume_extractor",
        description="Return the uploaded resume text for analysis. If no resume text is available, indicate that clearly.",
    )
    def extract_resume() -> str:
        return resume_text or "Resume text is not available."

    return extract_resume


def generate_letter_for_specific_job(resume_text: str):
    @tool(
        "generate_letter_for_specific_job",
        description="Generate a cover letter based on the uploaded resume and the provided job description.",
    )
    def _generate_letter_for_specific_job(job_description: str) -> str:
        resume_section = f"Candidate resume:\n{resume_text}\n\n" if resume_text else ""
        return (
            f"{resume_section}Create a professional cover letter for the following job description:\n{job_description}"
        )

    return _generate_letter_for_specific_job


@tool(
    "save_cover_letter_for_specific_job",
    description="Save a generated cover letter to a DOCX file and return the saved file path.",
)
def save_cover_letter_for_specific_job(text: str, filename: str = "temp/cover_letter.docx") -> str:
    return write_cover_letter_to_doc(text, filename)


@tool(
    "google_search",
    description="Search the web for relevant information based on a query.",
)
def get_google_search_results(query: str) -> str:
    return f"[Web Search Placeholder] Search requested for: {query}"


@tool(
    "scrape_website",
    description="Scrape a website and return the visible text content.",
)
def scrape_website(url: str) -> str:
    return f"[Web Scrape Placeholder] Scraping URL: {url}"
