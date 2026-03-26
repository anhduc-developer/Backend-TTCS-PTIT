import { useLocation, useNavigate } from "react-router-dom";
import { useState, useEffect } from 'react';
import { ICompany } from "@/types/backend";
import { callFetchCompanyById, callFetchJobsByCompany } from "@/config/api"; 
import styles from 'styles/client.module.scss';
import parse from 'html-react-parser';
import { Col, Divider, Row, Skeleton, Card, Empty, Tag, Typography } from "antd";
import { EnvironmentOutlined, DollarOutlined, HistoryOutlined, ThunderboltOutlined } from "@ant-design/icons";
import dayjs from 'dayjs';
import relativeTime from 'dayjs/plugin/relativeTime';

dayjs.extend(relativeTime);
const { Title, Text } = Typography;

const ClientCompanyDetailPage = (props: any) => {
    const [companyDetail, setCompanyDetail] = useState<ICompany | null>(null);
    const [listJobs, setListJobs] = useState<any[]>([]); 
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [isFetchingJobs, setIsFetchingJobs] = useState<boolean>(false);

    const navigate = useNavigate();
    const location = useLocation();
    const params = new URLSearchParams(location.search);
    const id = params?.get("id"); // ID công ty từ URL (?id=6)

    // Bước 1: Lấy thông tin công ty
    useEffect(() => {
        const init = async () => {
            if (id) {
                setIsLoading(true)
                const res = await callFetchCompanyById(id);
                if (res?.data) {
                    setCompanyDetail(res.data)
                }
                setIsLoading(false)
            }
        }
        init();
    }, [id]);

    // Bước 2: Tự động lấy Jobs của công ty đó
    useEffect(() => {
        const fetchJobs = async () => {
            if (companyDetail && companyDetail.id) {
                setIsFetchingJobs(true);
                // Tạo query đúng format backend: filter=company.id:6&page=1&size=10
                const query = `filter=company.id:${companyDetail.id}&page=1&size=100`;
                
                try {
                    // CHỈ TRUYỀN 1 THAM SỐ LÀ QUERY ĐỂ HẾT LỖI TS(2554)
                    const res = await callFetchJobsByCompany(query);
                    
                    // Kiểm tra data trả về (thường là res.data.result)
                    if (res?.data?.result) {
                        setListJobs(res.data.result);
                    }
                } catch (error) {
                    console.error("Lỗi 404 hoặc Fetch Jobs:", error);
                } finally {
                    setIsFetchingJobs(false);
                }
            }
        }
        fetchJobs();
    }, [companyDetail]);

    return (
        <div className={`${styles["container"]} ${styles["detail-job-section"]}`} style={{ padding: "20px" }}>
            {isLoading ? <Skeleton active /> :
                <>
                    <Row gutter={[20, 20]}>
                        {companyDetail && (
                            <>
                                <Col span={24} md={16}>
                                    <Title level={2}>{companyDetail.name}</Title>
                                    <Text type="secondary"><EnvironmentOutlined /> {companyDetail.address}</Text>
                                    <Divider />
                                    <div>{parse(companyDetail?.description ?? "")}</div>
                                </Col>
                                <Col span={24} md={8}>
                                    <Card style={{ textAlign: 'center' }}>
                                        <img
                                            width={150}
                                            src={`${import.meta.env.VITE_BACKEND_URL}/storage/company/${companyDetail?.logo}`}
                                            alt="logo"
                                        />
                                    </Card>
                                </Col>
                            </>
                        )}
                    </Row>

                    <Divider orientation="left">Vị trí đang tuyển dụng ({listJobs.length})</Divider>

                    <Row gutter={[20, 20]}>
                        {isFetchingJobs ? <Col span={24}><Skeleton active /></Col> : 
                            listJobs.length > 0 ? listJobs.map(job => (
                                <Col span={24} md={12} key={job.id}>
                                    <Card 
                                        hoverable 
                                        title={<span style={{ color: '#1677ff' }}>{job.name}</span>}
                                        extra={<Tag color="green"><ThunderboltOutlined /> Apply</Tag>}
                                        onClick={() => navigate(`/job/backend?id=${job.id}`)}
                                    >
                                        <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                                            <Text strong><DollarOutlined /> {job.salary?.toLocaleString()} VNĐ</Text>
                                            <Text type="secondary">{dayjs(job.createdAt).fromNow()}</Text>
                                        </div>
                                    </Card>
                                </Col>
                            )) : <Col span={24}><Empty description="Không có job nào" /></Col>
                        }
                    </Row>
                </>
            }
        </div>
    );
}

export default ClientCompanyDetailPage;