# Hệ Thống Quản Lý Việc Làm - Spring Boot RESTful API

[![Java](https://img.shields.io/badge/Java-17-orange)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.12-brightgreen)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19.2-blue)](https://reactjs.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue)](https://www.mysql.com/)

## 📖 Giới Thiệu

Đây là một dự án full-stack ứng dụng quản lý việc làm được phát triển trong quá trình thực tập của tôi. Dự án này giúp tôi áp dụng và củng cố kiến thức về phát triển phần mềm, từ backend API đến frontend giao diện người dùng. Hệ thống cho phép quản lý công ty, việc làm, người dùng, kỹ năng, và các chức năng liên quan khác.

**Mục tiêu học tập:**

- Học và thực hành Spring Boot framework cho backend development
- Phát triển frontend với React và Vite
- Tích hợp cơ sở dữ liệu MySQL với JPA
- Triển khai xác thực và phân quyền với Spring Security
- Thiết kế giao diện người dùng với Ant Design
- Phát triển API RESTful và tài liệu hóa với Swagger

## ✨ Tính Năng Chính

### Backend (Spring Boot)

- 🔐 **Xác thực và Phân quyền**: Spring Security + OAuth2
- 📊 **Quản lý Cơ sở dữ liệu**: JPA/Hibernate với MySQL
- 📧 **Gửi Email**: Tích hợp Spring Mail
- 📚 **Tài liệu API**: Swagger/OpenAPI
- 🧪 **Kiểm thử**: Unit testing với JUnit 5
- 📈 **Giám sát**: Spring Boot Actuator

### Frontend (React)

- 🎨 **Giao diện đẹp**: Ant Design UI components
- 🚀 **Hiệu suất cao**: Vite build tool
- 🛡️ **Bảo mật**: Protected routes với authentication
- 📱 **Responsive**: Thiết kế đáp ứng trên mọi thiết bị
- 📊 **Trực quan hóa dữ liệu**: Charts với Ant Design Plots
- ✏️ **Soạn thảo văn bản**: Rich text editor với React Quill

### Các Module Chính

- 👥 **Quản lý Người dùng**: Đăng ký, đăng nhập, phân quyền
- 🏢 **Quản lý Công ty**: Thêm, sửa, xóa thông tin công ty
- 💼 **Quản lý Việc làm**: Tạo và quản lý các vị trí tuyển dụng
- 🎯 **Quản lý Kỹ năng**: Danh sách kỹ năng và phân loại
- 📄 **Quản lý CV**: Xem và cập nhật hồ sơ ứng viên
- 🔑 **Quản lý Vai trò và Quyền**: Phân quyền chi tiết

## 🛠️ Công Nghệ Sử Dụng

### Backend

| Công nghệ       | Phiên bản | Mục đích              |
| --------------- | --------- | --------------------- |
| Spring Boot     | 3.5.12    | Framework chính       |
| Java            | 17        | Ngôn ngữ lập trình    |
| MySQL           | 8.0+      | Cơ sở dữ liệu         |
| Spring Security | Latest    | Xác thực & phân quyền |
| Spring Data JPA | Latest    | ORM                   |
| Spring Mail     | Latest    | Gửi email             |
| Lombok          | 9.2.0     | Giảm boilerplate code |
| JUnit 5         | Latest    | Kiểm thử              |
| Gradle          | Latest    | Công cụ build         |

### Frontend

| Công nghệ    | Phiên bản | Mục đích       |
| ------------ | --------- | -------------- |
| React        | 19.2.4    | Framework UI   |
| Vite         | Latest    | Công cụ build  |
| Ant Design   | 6.3.3     | Thư viện UI    |
| React Router | 7.13.1    | Điều hướng     |
| Axios        | Latest    | HTTP client    |
| React Quill  | 3.8.3     | Editor văn bản |
| ESLint       | 9.39.4    | Kiểm tra code  |

## 📋 Yêu Cầu Hệ Thống

- **Java**: 17 hoặc cao hơn
- **Node.js**: 16+ (cho frontend)
- **MySQL**: 8.0+
- **Gradle**: 7.0+
- **Git**: Để clone repository

## 🚀 Hướng Dẫn Cài Đặt và Chạy

### 1. Clone Repository

```bash
git clone <repository-url>
cd Spring-Boot-RESTful-API
```

### 2. Cài Đặt Backend

```bash
cd Backend

# Cấu hình cơ sở dữ liệu trong application.properties
# Tạo database MySQL với tên 'job_management'

# Chạy backend
./gradlew bootRun
```

Backend sẽ chạy trên `http://localhost:8080`

### 3. Cài Đặt Frontend

```bash
cd ../Frontend

# Cài đặt dependencies
npm install

# Chạy frontend
npm run dev
```

Frontend sẽ chạy trên `http://localhost:5173`

### 4. Truy Cập Ứng Dụng

- **Frontend**: http://localhost:5173
- **API Documentation**: http://localhost:8080/swagger-ui.html
- **Backend API**: http://localhost:8080/api

## 📁 Cấu Trúc Dự Án

```
Spring-Boot-RESTful-API/
├── Backend/                    # Spring Boot API Server
│   ├── src/main/java/vn/hunter/job/  # Source code Java
│   ├── src/main/resources/           # Cấu hình và templates
│   ├── build.gradle.kts             # Gradle config
│   └── gradlew                      # Gradle wrapper
├── Frontend/                   # React Vite Application
│   ├── src/
│   │   ├── components/         # React components
│   │   ├── pages/             # Các trang
│   │   ├── services/          # API services
│   │   └── styles/            # CSS styles
│   ├── package.json           # Dependencies
│   └── vite.config.js         # Vite config
└── README.md                  # Tài liệu này
```

## 🤝 Đóng Góp

Dự án này là phần của quá trình học tập của tôi. Nếu bạn có góp ý hoặc muốn cải thiện, hãy tạo issue hoặc pull request!

## 👨‍💻 Tác Giả

**Mai Anh Đức** - Intern Developer

- Email: tomorrowduc@gmail.com
- GitHub: [anhduc-developer](https://github.com/anhduc-developer)

## 📄 Giấy Phép

Dự án này được phát triển cho mục đích học tập và không có giấy phép cụ thể.

---

_Xin cảm ơn đã quan tâm đến dự án của tôi!_
│ ├── src/
│ │ ├── components/ # React components
│ │ ├── pages/ # Page components
│ │ ├── services/ # API services
│ │ ├── config/ # Configuration
│ │ └── styles/ # Global styles
│ ├── package.json
│ └── vite.config.js
└── README.md # This file

````

## 🚀 Hướng Dẫn Cài Đặt

### Prerequisites

- **Java 17+**
- **Node.js 16+** và **npm/yarn**
- **MySQL Server 8.0+**
- **Git**

### Cài Đặt Backend

1. **Clone repository**

   ```bash
   git clone <repository-url>
   cd Spring-Boot-RESTful-API/Backend
````

2. **Cấu hình Database**
   - Tạo database MySQL
   - Cập nhật `src/resources/application.properties`:
     ```properties
     spring.datasource.url=jdbc:mysql://localhost:3306/job_database
     spring.datasource.username=root
     spring.datasource.password=your_password
     spring.jpa.hibernate.ddl-auto=update
     ```

3. **Build và chạy Backend**

   ```bash
   # Build
   ./gradlew build

   # Chạy development
   ./gradlew bootRun
   ```

   Backend sẽ chạy tại: `http://localhost:8080`

4. **Truy cập API Documentation**
   - Swagger UI: `http://localhost:8080/swagger-ui.html`
   - OpenAPI JSON: `http://localhost:8080/v3/api-docs`

### Cài Đặt Frontend

1. **Navigate to Frontend**

   ```bash
   cd Spring-Boot-RESTful-API/Frontend
   ```

2. **Cài đặt Dependencies**

   ```bash
   npm install
   # hoặc
   yarn install
   ```

3. **Cấu hình API endpoint**
   - Chỉnh sửa `src/config/utils.js` với Backend URL

4. **Chạy Development Server**

   ```bash
   npm run dev
   # hoặc
   yarn dev
   ```

   Frontend sẽ chạy tại: `http://localhost:5173`

5. **Build cho Production**
   ```bash
   npm run build
   # hoặc
   yarn build
   ```

## 📖 Tính Năng Chính

### Admin Dashboard

- 👥 Quản lý Người Dùng
- 🏢 Quản lý Công Ty
- 💼 Quản lý Việc Làm
- 👤 Quản lý Quyền Hạn
- 🎯 Quản lý Vai Trò
- 🛠️ Quản lý Kỹ Năng
- 📄 Quản lý Hồ Sơ
- 📊 Dashboard thống kê

### Client Features

- 🏠 Trang chủ
- 👤 Trang hồ sơ cá nhân
- 📋 Danh sách công ty
- 💼 Danh sách việc làm
- 📝 Đơn ứng tuyển

## 🔐 Security Features

- **Authentication**: OAuth2 + JWT
- **Authorization**: Role-Based Access Control (RBAC)
- **Spring Security**: Bảo vệ các endpoint API
- **Password Encryption**: Sử dụng BCrypt

## 📝 API Documentation

API Documentation đầy đủ có sẵn tại Swagger UI sau khi Backend chạy:

- Navigate to: `http://localhost:8080/swagger-ui.html`

## 🧪 Testing

### Backend Testing

```bash
cd Backend
./gradlew test
```

### Frontend Testing

```bash
cd Frontend
npm run lint
```

## 🐛 Troubleshooting

### Backend Issues

- **Port already in use**: Thay đổi port trong `application.properties`
- **Database connection failed**: Kiểm tra MySQL đang chạy và credentials đúng
- **Dependencies error**: Chạy `./gradlew clean build`

### Frontend Issues

- **npm install failed**: Xóa `node_modules` và `package-lock.json`, rồi chạy `npm install` lại
- **Vite port conflict**: Chạy `npm run dev -- --port 3000`

## 👨‍💻 Quy Trình Phát Triển

### Backend Development

```bash
cd Backend
./gradlew bootRun    # Auto-reload khi file thay đổi
```

### Frontend Development

```bash
cd Frontend
npm run dev          # Auto-reload khi file thay đổi
```

## 📦 Build for Production

### Backend

```bash
cd Backend
./gradlew build -x test  # Build without running tests
```

### Frontend

```bash
cd Frontend
npm run build
```

## 📄 Project Structure Details

### Backend Structure

- **Controllers**: REST API endpoints
- **Services**: Business logic
- **Repositories**: Database access (JPA)
- **Entities**: Domain models
- **DTOs**: Data Transfer Objects
- **Config**: Spring configuration
- **Security**: Authentication & Authorization

### Frontend Structure

- **components/**: Reusable React components
- **pages/**: Page-level components
- **services/**: API communication
- **context/**: React Context for state management
- **config/**: Configuration files
- **assets/**: Images, icons, etc.
- **styles/**: CSS files

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Support

Nếu bạn gặp bất kỳ vấn đề nào hoặc có câu hỏi, vui lòng tạo một issue trên repository.

---

**Last Updated**: April 7, 2026
