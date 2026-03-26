import { Button, Col, Form, Row, Select, notification, Input } from "antd"; // Thêm Input
import {
  EnvironmentOutlined,
  MonitorOutlined,
  SearchOutlined,
} from "@ant-design/icons"; // Thêm icon search
import { LOCATION_LIST } from "@/config/utils";
import { ProForm } from "@ant-design/pro-components";
import { useEffect, useState } from "react";
import { callFetchAllSkill } from "@/config/api";
import { useLocation, useNavigate, useSearchParams } from "react-router-dom";

const SearchClient = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const optionsLocations = LOCATION_LIST;
  const [form] = Form.useForm();
  const [optionsSkills, setOptionsSkills] = useState<
    { label: string; value: string }[]
  >([]);
  const [searchParams] = useSearchParams();

  useEffect(() => {
    if (location.search) {
      const queryLocation = searchParams.get("location");
      const querySkills = searchParams.get("skills");
      const queryName = searchParams.get("name"); // Lấy name từ URL

      if (queryLocation)
        form.setFieldValue("location", queryLocation.split(","));
      if (querySkills) form.setFieldValue("skills", querySkills.split(","));
      if (queryName) form.setFieldValue("name", queryName); // Set vào form
    }
  }, [location.search]);

  useEffect(() => {
    fetchSkill();
  }, []);

  const fetchSkill = async () => {
    let query = `page=1&size=100&sort=createdAt,desc`;
    const res = await callFetchAllSkill(query);
    if (res && res.data) {
      const arr =
        res?.data?.result?.map((item) => ({
          label: item.name as string,
          value: (item.id + "") as string,
        })) ?? [];
      setOptionsSkills(arr);
    }
  };

  // ... các import giữ nguyên, thêm Input từ "antd"
  const onFinish = async (values: any) => {
    const { location, skills, name } = values;
    const params = new URLSearchParams();

    if (name) params.append("name", name);
    if (location?.length) params.append("location", location.join(","));
    if (skills?.length) params.append("skills", skills.join(","));

    const queryString = params.toString();

    if (!queryString) {
      notification.warning({
        message: "Thông báo",
        description: "Vui lòng nhập từ khóa hoặc chọn tiêu chí tìm kiếm",
      });
      return;
    }

    // Chuyển hướng sang trang danh sách job với query string mới
    navigate(`/job?${queryString}`);
  };

  return (
    <ProForm
      form={form}
      onFinish={onFinish}
      submitter={{ render: () => <></> }}
    >
      <Row gutter={[20, 20]}>
        <Col span={24}>
          <h2>Việc Làm IT Cho Developer</h2>
        </Col>

        {/* Thêm ô nhập tên Job */}
        <Col span={24} md={8}>
          <ProForm.Item name="name">
            <Input
              prefix={<SearchOutlined />}
              placeholder="Tên công việc, vị trí..."
              allowClear
            />
          </ProForm.Item>
        </Col>

        <Col span={24} md={8}>
          <ProForm.Item name="skills">
            <Select
              mode="multiple"
              allowClear
              suffixIcon={null}
              style={{ width: "100%" }}
              placeholder={
                <>
                  <MonitorOutlined /> Kỹ năng...
                </>
              }
              optionLabelProp="label"
              options={optionsSkills}
            />
          </ProForm.Item>
        </Col>

        <Col span={12} md={4}>
          <ProForm.Item name="location">
            <Select
              mode="multiple"
              allowClear
              suffixIcon={null}
              style={{ width: "100%" }}
              placeholder={
                <>
                  <EnvironmentOutlined /> Địa điểm...
                </>
              }
              optionLabelProp="label"
              options={optionsLocations}
            />
          </ProForm.Item>
        </Col>

        <Col span={12} md={4}>
          <Button type="primary" onClick={() => form.submit()} block>
            Search
          </Button>
        </Col>
      </Row>
    </ProForm>
  );
};

export default SearchClient;
