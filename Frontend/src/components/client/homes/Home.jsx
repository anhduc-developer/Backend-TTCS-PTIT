import React, { useState, useMemo, useEffect } from "react";
import {
  Row,
  Col,
  Card,
  Typography,
  Tag,
  Pagination,
  Divider,
  Button,
  Input,
  Space,
  Slider,
  Empty,
} from "antd";
import {
  EnvironmentOutlined,
  EyeOutlined,
  SearchOutlined,
  ShopOutlined,
  DollarOutlined,
} from "@ant-design/icons";
import { useNavigate } from "react-router-dom";
import {
  getCvRecommendations,
  uploadCvRecommendJobs,
} from "../../../services/api.service";
import dayjs from "dayjs";
import relativeTime from "dayjs/plugin/relativeTime";
import "dayjs/locale/vi";
import "dayjs/locale/en";
import { useTranslation } from "react-i18next";

const { Title, Text } = Typography;

const Home = (props) => {
  const { t, i18n } = useTranslation();
  const { companyData = [], jobData = [] } = props;
  const navigate = useNavigate();

  const [jobSearch, setJobSearch] = useState("");
  const [comSearch, setComSearch] = useState("");
  const [salaryRange, setSalaryRange] = useState([0, 100000000]);

  const [jobCurrent, setJobCurrent] = useState(1);
  const [jobPageSize, setJobPageSize] = useState(6);
  const [recommendPage, setRecommendPage] = useState(1);
  const [recommendPageSize] = useState(6);
  const [comCurrent, setComCurrent] = useState(1);
  const [comPageSize, setComPageSize] = useState(8);
  const [recommendations, setRecommendations] = useState([]);
  const [cvSkills, setCvSkills] = useState([]);
  const [isCvUploading, setIsCvUploading] = useState(false);
  const [cvError, setCvError] = useState("");
  const [cvId, setCvId] = useState(null);
  const [cvFileName, setCvFileName] = useState("");

  dayjs.extend(relativeTime);
  useEffect(() => {
    dayjs.locale(i18n.language === "vi" ? "vi" : "en");
  }, [i18n.language]);
  const loadCvRecommendations = async (id) => {
    console.log("Loading CV recommendations for ID:", id);
    try {
      const res = await getCvRecommendations(id);
      console.log("Loaded CV data:", res.data);
      if (res && res.data) {
        setCvId(id);
        setCvFileName(res.data.fileName || "");
        setCvSkills(res.data.skills || []);
        setRecommendations(res.data.jobs || []);
        setRecommendPage(1);
      }
    } catch (error) {
      console.error("Failed to load CV recommendations:", error);
      localStorage.removeItem("cvId");
    }
  };

  useEffect(() => {
    const savedCvId = localStorage.getItem("cvId");
    if (savedCvId) {
      loadCvRecommendations(parseInt(savedCvId, 10));
    }
  }, []);

  const removeVietnameseTones = (str) => {
    if (!str) return "";
    return str
      .normalize("NFD")
      .replace(/[\u0300-\u036f]/g, "")
      .replace(/đ/g, "d")
      .replace(/Đ/g, "D")
      .toLowerCase()
      .trim();
  };

  const handleCvUpload = async (event) => {
    const file = event.target.files?.[0];
    if (!file) {
      return;
    }

    const allowedMimeTypes = [
      "application/pdf",
      "application/msword",
      "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
    ];
    const allowedExtensions = /\.(pdf|doc|docx)$/i;
    if (
      !allowedExtensions.test(file.name) ||
      !allowedMimeTypes.includes(file.type)
    ) {
      setCvError("Chỉ cho phép tệp PDF, DOC hoặc DOCX");
      return;
    }

    setCvError("");
    setIsCvUploading(true);
    try {
      const res = await uploadCvRecommendJobs(file);
      console.log("Upload response:", res.data);
      if (res && res.data) {
        setCvId(res.data.cvId);
        setCvFileName(res.data.fileName || file.name);
        setCvSkills(res.data.skills || []);
        setRecommendations(res.data.jobs || []);
        setRecommendPage(1);
        localStorage.setItem("cvId", res.data.cvId.toString());
      }
    } catch (error) {
      setCvError(
        error?.response?.data?.message ||
          "Tải CV không thành công. Vui lòng thử lại.",
      );
      setRecommendations([]);
      setCvSkills([]);
      setCvFileName("");
    }
    setIsCvUploading(false);
  };

  const filteredJobs = useMemo(() => {
    const searchTerm = removeVietnameseTones(jobSearch);

    return jobData
      .filter((job) => {
        const jobName = removeVietnameseTones(job.name);
        const companyName = removeVietnameseTones(job.company?.name);
        const location = removeVietnameseTones(job.location);

        const matchInfo =
          jobName.includes(searchTerm) ||
          companyName.includes(searchTerm) ||
          location.includes(searchTerm);

        const matchSkills = job.skills?.some((s) =>
          removeVietnameseTones(s.name).includes(searchTerm),
        );

        const jobSalary = job.salary || 0;
        const matchSalary =
          jobSalary >= salaryRange[0] && jobSalary <= salaryRange[1];

        return (
          (matchInfo || matchSkills) &&
          matchSalary &&
          job.active &&
          job.status === "APPROVED" &&
          job.hot
        );
      })
      .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
  }, [jobData, jobSearch, salaryRange]);

  const currentJobs = filteredJobs.slice(
    (jobCurrent - 1) * jobPageSize,
    jobCurrent * jobPageSize,
  );

  const filteredCompanies = useMemo(() => {
    const searchTerm = removeVietnameseTones(comSearch);
    return companyData.filter((c) => {
      const comName = removeVietnameseTones(c.name);
      const comAddr = removeVietnameseTones(c.address);
      return (
        (comName.includes(searchTerm) || comAddr.includes(searchTerm)) &&
        c.outstanding
      );
    });
  }, [companyData, comSearch]);

  const currentCompanies = filteredCompanies.slice(
    (comCurrent - 1) * comPageSize,
    comCurrent * comPageSize,
  );

  const jobCountMap = jobData.reduce((acc, job) => {
    if (job.company?.id && job.active && job.status === "APPROVED") {
      acc[+job.company.id] = (acc[+job.company.id] || 0) + 1;
    }
    return acc;
  }, {});

  const handleViewDetail = (job) =>
    navigate(`/job/${job.id}`, { state: { job } });

  const handleApplyJob = (job) =>
    navigate(`/job/${job.id}`, { state: { job, fromCvRecommend: true } });

  const handleViewDetailCompany = (company) =>
    navigate(`/company/${company.id}`, { state: { company } });

  return (
    <div style={{ maxWidth: 1200, margin: "0 auto", padding: "20px 10px" }}>
      {/* JOB */}
      <div style={{ marginBottom: 50 }}>
        <Title level={3}> {t("home.hotJobs")}</Title>
        <Card style={{ marginBottom: 20 }}>
          <Row gutter={[32, 16]}>
            <Col xs={24} md={12}>
              <Text strong>
                <SearchOutlined /> {t("home.searchJob")}
              </Text>
              <Input
                placeholder={t("home.searchPlaceholder")}
                allowClear
                onChange={(e) => {
                  setJobSearch(e.target.value);
                  setJobCurrent(1);
                }}
              />
            </Col>

            <Col xs={24} md={12}>
              <Text strong>
                <DollarOutlined /> {t("home.salary")}
              </Text>
              <Slider
                range
                step={1000000}
                min={0}
                max={100000000}
                defaultValue={[0, 100000000]}
                onChange={(val) => {
                  setSalaryRange(val);
                  setJobCurrent(1);
                }}
              />
            </Col>
          </Row>
        </Card>

        <Card style={{ marginBottom: 20 }}>
          <Title level={4}>Gợi ý việc làm theo CV</Title>
          <Text>
            Tải lên file CV của bạn (PDF/DOC/DOCX) để hệ thống tự động đề xuất
            công việc phù hợp theo kỹ năng.
          </Text>
          <div
            style={{
              display: "flex",
              flexDirection: "column",
              gap: 12,
              maxWidth: 420,
            }}
          >
            <label
              style={{
                position: "relative",
                border: "2px dashed #d1d5db",
                borderRadius: 14,
                padding: 24,
                cursor: isCvUploading ? "not-allowed" : "pointer",
                background: "lightblue",
                transition: "all 0.3s ease",
                opacity: isCvUploading ? 0.6 : 1,
              }}
            >
              <input
                type="file"
                accept=".pdf,.doc,.docx"
                onChange={handleCvUpload}
                disabled={isCvUploading}
                style={{
                  position: "absolute",
                  inset: 0,
                  opacity: 0,
                  cursor: "pointer",
                }}
              />

              <div
                style={{
                  display: "flex",
                  alignItems: "center",
                  gap: 14,
                  justifyContent: "center",
                }}
              >
                <div>
                  <div
                    style={{
                      fontSize: 15,
                      fontWeight: 600,
                      color: "#111827",
                    }}
                  >
                    {cvFileName
                      ? "Đã tải lên: " + cvFileName
                      : "Tải CV của bạn"}
                  </div>

                  <div
                    style={{
                      fontSize: 13,
                      color: "#6b7280",
                      marginTop: 4,
                    }}
                  >
                    {cvFileName
                      ? "Kéo thả hoặc click để thay đổi file (.pdf, .doc, .docx)"
                      : "Kéo thả hoặc click để chọn file (.pdf, .doc, .docx)"}
                  </div>
                </div>
              </div>
            </label>

            {isCvUploading && (
              <div
                style={{
                  display: "flex",
                  alignItems: "center",
                  gap: 10,
                  fontSize: 13,
                  color: "#6366f1",
                }}
              >
                <div
                  style={{
                    width: 16,
                    height: 16,
                    border: "2px solid #c7d2fe",
                    borderTop: "2px solid #4f46e5",
                    borderRadius: "50%",
                    animation: "spin 0.8s linear infinite",
                  }}
                />
                <span>Đang phân tích CV...</span>
              </div>
            )}

            {/* keyframes hack cho inline */}
            <style>
              {`
      @keyframes spin {
        to { transform: rotate(360deg); }
      }
    `}
            </style>
          </div>
          {cvError && (
            <Text type="danger" style={{ display: "block", marginTop: 12 }}>
              {cvError}
            </Text>
          )}
          {cvId && (
            <Button
              type="link"
              danger
              onClick={() => {
                setCvId(null);
                setCvFileName("");
                setCvSkills([]);
                setRecommendations([]);
                localStorage.removeItem("cvId");
              }}
              style={{ marginTop: 12 }}
            >
              Xóa CV đã tải
            </Button>
          )}
          {cvSkills.length > 0 && (
            <div style={{ marginTop: 16 }}>
              <Text strong>Kỹ năng phát hiện:</Text>
              <div style={{ marginTop: 8 }}>
                <Space wrap>
                  {cvSkills.map((skill) => (
                    <Tag key={skill}>{skill}</Tag>
                  ))}
                </Space>
              </div>
            </div>
          )}
          <div style={{ marginTop: 16 }}>
            <Text strong>Việc làm gợi ý:</Text>
            {recommendations.length > 0 ? (
              <>
                <Row gutter={[16, 16]} style={{ marginTop: 12 }}>
                  {recommendations
                    .slice(
                      (recommendPage - 1) * recommendPageSize,
                      recommendPage * recommendPageSize,
                    )
                    .map((job) => (
                      <Col xs={24} sm={12} key={job.id}>
                        <Card
                          hoverable
                          style={{
                            borderRadius: 18,
                            border: "1px solid #f0f0f0",
                            boxShadow: "0 14px 40px rgba(0, 0, 0, 0.05)",
                            cursor: "pointer",
                          }}
                          bodyStyle={{ padding: 20 }}
                          onClick={() => handleViewDetail(job)}
                        >
                          <div
                            style={{
                              display: "flex",
                              justifyContent: "space-between",
                              alignItems: "flex-start",
                              gap: 12,
                            }}
                          >
                            <div style={{ flex: 1 }}>
                              <div
                                style={{
                                  fontWeight: 700,
                                  fontSize: 16,
                                  lineHeight: "24px",
                                  marginBottom: 6,
                                }}
                              >
                                {job.name}
                              </div>
                              <div>
                                <Text strong>{job.companyName}</Text>
                                <div
                                  style={{
                                    color: "#8c8c8c",
                                    fontSize: 13,
                                    marginTop: 6,
                                  }}
                                >
                                  <EnvironmentOutlined /> {job.location}
                                </div>
                              </div>
                            </div>
                            <Tag
                              color={job.matchScore >= 2 ? "green" : "blue"}
                              style={{ fontWeight: 700 }}
                            >
                              Match {job.matchScore}
                            </Tag>
                          </div>

                          <div style={{ marginTop: 14 }}>
                            <Space wrap>
                              {job.matchedSkills.map((skill) => (
                                <Tag key={skill} color="cyan">
                                  {skill.charAt(0).toUpperCase() +
                                    skill.slice(1)}
                                </Tag>
                              ))}
                            </Space>
                          </div>

                          <div
                            style={{
                              marginTop: 18,
                              display: "flex",
                              justifyContent: "space-between",
                              alignItems: "center",
                              gap: 12,
                            }}
                          >
                            <div>
                              <Text strong style={{ color: "#1890ff" }}>
                                {job.salary?.toLocaleString() || 0} đ
                              </Text>
                            </div>
                            <Space>
                              <Button
                                type="primary"
                                size="small"
                                style={{ borderRadius: 8 }}
                                onClick={(e) => {
                                  e.stopPropagation();
                                  handleApplyJob(job);
                                }}
                              >
                                Ứng tuyển ngay
                              </Button>
                            </Space>
                          </div>
                        </Card>
                      </Col>
                    ))}
                </Row>
                {recommendations.length > recommendPageSize && (
                  <Pagination
                    current={recommendPage}
                    pageSize={recommendPageSize}
                    total={recommendations.length}
                    onChange={(page) => setRecommendPage(page)}
                    style={{ marginTop: 16, textAlign: "center" }}
                  />
                )}
              </>
            ) : cvId !== null ? (
              <Text
                type="secondary"
                style={{ display: "block", marginTop: 12 }}
              >
                Không tìm thấy việc làm phù hợp trên dữ liệu hiện tại.
              </Text>
            ) : null}
          </div>
        </Card>

        <Row gutter={[20, 24]}>
          {currentJobs.length > 0 ? (
            currentJobs.map((job) => (
              <Col xs={24} sm={12} md={8} key={job.id}>
                <Card
                  hoverable
                  onClick={() => handleViewDetail(job)}
                  style={{
                    borderRadius: 16,
                    height: "100%",
                    border: "1px solid #f0f0f0",
                    transition: "all 0.25s ease",
                    position: "relative", // 👈 bắt buộc
                    paddingTop: 8,
                  }}
                  bodyStyle={{ padding: 16 }}
                >
                  {/* HOT FIXED TOP RIGHT */}
                  {job.hot && (
                    <Tag
                      color="volcano"
                      style={{
                        position: "absolute",
                        top: 12,
                        right: 12,
                        zIndex: 2,
                        fontWeight: 600,
                        borderRadius: 6,
                      }}
                    >
                      HOT
                    </Tag>
                  )}

                  {/* HEADER */}
                  <div style={{ display: "flex", gap: 12, marginBottom: 12 }}>
                    <img
                      src={
                        job.company?.logo
                          ? `${import.meta.env.VITE_BACKEND_URL}/storage/company/${job.company.logo}`
                          : "https://via.placeholder.com/50"
                      }
                      onError={(e) => {
                        e.currentTarget.src = "https://via.placeholder.com/50";
                      }}
                      alt="logo"
                      style={{
                        width: 48,
                        height: 48,
                        objectFit: "contain",
                        borderRadius: 8,
                        border: "1px solid #eee",
                        padding: 4,
                      }}
                    />

                    {/* 👇 CHỖ QUAN TRỌNG */}
                    <div style={{ flex: 1, paddingRight: 50 }}>
                      {/* paddingRight để tránh bị HOT đè */}

                      <div
                        style={{
                          fontWeight: 600,
                          fontSize: 15,
                          lineHeight: "20px",
                          display: "-webkit-box",
                          WebkitLineClamp: 2,
                          WebkitBoxOrient: "vertical",
                          overflow: "hidden",
                        }}
                      >
                        {job.name}
                      </div>

                      <Text type="secondary" style={{ fontSize: 12 }}>
                        {job.company?.name}
                      </Text>
                    </div>
                  </div>

                  {/* SKILLS */}
                  <div style={{ marginBottom: 10 }}>
                    <Space size={[6, 6]} wrap>
                      {job.skills?.slice(0, 3).map((s, i) => (
                        <Tag key={i} color="blue" style={{ fontSize: 11 }}>
                          {s.name}
                        </Tag>
                      ))}
                    </Space>
                  </div>

                  {/* LOCATION */}
                  <div
                    style={{ fontSize: 12, color: "#666", marginBottom: 10 }}
                  >
                    <EnvironmentOutlined /> {job.location}
                  </div>

                  {/* FOOTER */}
                  <div
                    style={{
                      display: "flex",
                      justifyContent: "space-between",
                      borderTop: "1px dashed #eee",
                      paddingTop: 10,
                      marginTop: 10,
                    }}
                  >
                    <Text strong style={{ color: "#ff4d4f" }}>
                      {job.salary?.toLocaleString()} đ
                    </Text>

                    <Text type="secondary" style={{ fontSize: 11 }}>
                      {dayjs(job.createdAt)
                        .locale(i18n.language === "vi" ? "vi" : "en")
                        .fromNow()}
                    </Text>
                  </div>
                </Card>
              </Col>
            ))
          ) : (
            <Col span={24}>
              <Empty description={t("home.noJobs")} />
            </Col>
          )}
        </Row>

        <Pagination
          current={jobCurrent}
          pageSize={jobPageSize}
          total={filteredJobs.length}
          onChange={(p) => setJobCurrent(p)}
        />
      </div>

      {/* COMPANY */}
      <div>
        <Title level={3}> {t("home.topCompanies")}</Title>

        <Input
          placeholder={t("home.searchCompany")}
          prefix={<ShopOutlined />}
          allowClear
          onChange={(e) => {
            setComSearch(e.target.value);
            setComCurrent(1);
          }}
        />

        <Divider />

        <Row gutter={[16, 24]}>
          {currentCompanies.length > 0 ? (
            currentCompanies.map((c) => {
              const count = jobCountMap[+c.id] || 0;

              return (
                <Col xs={12} md={6} key={c.id}>
                  <Card
                    hoverable
                    onClick={() => handleViewDetailCompany(c)}
                    style={{
                      borderRadius: 16,
                      height: "100%",
                      display: "flex",
                      flexDirection: "column",
                      justifyContent: "space-between",
                      textAlign: "center",
                      padding: 12,
                      transition: "all 0.25s ease",
                    }}
                    bodyStyle={{ padding: 12 }}
                    onMouseEnter={(e) => {
                      e.currentTarget.style.transform = "translateY(-4px)";
                      e.currentTarget.style.boxShadow =
                        "0 8px 20px rgba(0,0,0,0.08)";
                    }}
                    onMouseLeave={(e) => {
                      e.currentTarget.style.transform = "none";
                      e.currentTarget.style.boxShadow = "none";
                    }}
                  >
                    {/* LOGO (FIX CHUẨN) */}
                    <div
                      style={{
                        height: 70,
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "center",
                        marginBottom: 10,
                      }}
                    >
                      <img
                        src={
                          c?.logo
                            ? `${import.meta.env.VITE_BACKEND_URL}/storage/company/${c.logo}`
                            : "https://via.placeholder.com/80"
                        }
                        onError={(e) => {
                          e.currentTarget.src =
                            "https://via.placeholder.com/80";
                        }}
                        alt={c.name}
                        style={{
                          maxHeight: 60,
                          maxWidth: "80%",
                          objectFit: "contain",
                        }}
                      />
                    </div>

                    {/* NAME (FIX KHÔNG VỠ) */}
                    <div
                      style={{
                        fontWeight: 600,
                        fontSize: 14,
                        marginBottom: 8,
                        minHeight: 40,
                        display: "-webkit-box",
                        WebkitLineClamp: 2,
                        WebkitBoxOrient: "vertical",
                        overflow: "hidden",
                      }}
                    >
                      {c.name}
                    </div>

                    {/* JOB COUNT */}
                    <Tag style={{ marginBottom: 10 }}>
                      {count > 0
                        ? `${count} ${t("home.jobs")}`
                        : t("home.noCompanyJobs")}
                    </Tag>

                    {/* BUTTON */}
                    <Button icon={<EyeOutlined />} block>
                      {t("common.detail")}
                    </Button>
                  </Card>
                </Col>
              );
            })
          ) : (
            <Col span={24}>
              <Empty description={t("home.noCompanyJobs")} />
            </Col>
          )}
        </Row>

        <Pagination
          current={comCurrent}
          pageSize={comPageSize}
          total={filteredCompanies.length}
          onChange={(p) => setComCurrent(p)}
        />
      </div>
    </div>
  );
};

export default Home;
