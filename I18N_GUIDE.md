# I18N (Internationalization) Implementation Guide

## Overview

This document explains how to use the English/Vietnamese language switching feature integrated into the project.

---

## Frontend (React) - i18next

### Installation

Dependencies are already installed:

```bash
npm install i18next i18next-browser-languagedetector i18next-http-backend react-i18next
```

### Files Structure

```
Frontend/
├── public/
│   └── locales/
│       ├── en/
│       │   └── common.json       # English translations
│       └── vi/
│           └── common.json       # Vietnamese translations
├── src/
│   ├── i18n.js                  # i18n configuration
│   └── components/
│       └── LanguageSwitcher.jsx  # Language switcher component
└── main.jsx                      # Updated to initialize i18n
```

### Usage in Components

#### 1. Using useTranslation Hook

```jsx
import { useTranslation } from "react-i18next";

function MyComponent() {
  const { t, i18n } = useTranslation();

  return (
    <div>
      <h1>{t("header.dashboard")}</h1>
      <p>{t("form.name")}</p>
      <p>Current language: {i18n.language}</p>
    </div>
  );
}
```

#### 2. Using Language Switcher Component

The LanguageSwitcher component is already integrated in:

- Admin Layout (`admin/layout.admin.jsx`)
- Client Header (`client/layout/header.client.jsx`)

It allows users to switch between English and Vietnamese from any page.

#### 3. Translation Keys

Available translation keys are organized by categories:

**Header & Navigation:**

- `header.dashboard`
- `header.company`
- `header.job`
- `header.resume`
- `header.permission`
- `header.role`
- `header.skill`
- `header.user`
- `header.profile`
- `header.logout`
- `header.login`
- `header.register`
- `header.language`

**Form Labels:**

- `form.name`
- `form.email`
- `form.password`
- `form.confirmPassword`
- `form.age`
- `form.gender`
- `form.address`
- `form.phone`
- `form.description`
- `form.submitButton`
- `form.cancelButton`
- `form.searchPlaceholder`
- `form.selectPlaceholder`

**Validation Messages:**

- `validation.required`
- `validation.emailFormat`
- `validation.minLength`
- `validation.maxLength`
- `validation.passwordMatch`
- `validation.ageMin`
- `validation.ageMax`

**Response Messages:**

- `message.success`
- `message.error`
- `message.warning`
- `message.info`
- `message.confirmDelete`
- `message.deleteSuccess`
- `message.createSuccess`
- `message.updateSuccess`
- `message.loading`
- `message.noData`
- `message.unauthorized`
- `message.forbidden`
- `message.notFound`
- `message.serverError`

#### 4. Adding New Translations

To add new translations:

1. **Add to English file** (`public/locales/en/common.json`):

```json
{
  "newFeature": {
    "title": "New Feature",
    "description": "This is a new feature"
  }
}
```

2. **Add to Vietnamese file** (`public/locales/vi/common.json`):

```json
{
  "newFeature": {
    "title": "Tính năng mới",
    "description": "Đây là một tính năng mới"
  }
}
```

3. **Use in component**:

```jsx
<h1>{t('newFeature.title')}</h1>
<p>{t('newFeature.description')}</p>
```

#### 5. Advanced Features

```jsx
// Interpolation - Replace variables in text
const name = "John";
<p>{t('Welcome, {{name}}', { name })}</p>

// Pluralization - Different text based on count
<p>{t('items', { count: 3 })}</p>

// Changing language programmatically
i18n.changeLanguage('vi');  // Switch to Vietnamese
i18n.changeLanguage('en');  // Switch to English
```

---

## Backend (Spring Boot) - Message Source

### Installation

No additional dependencies needed. Spring Boot has built-in support for MessageSource.

### Files Structure

```
Backend/
├── src/main/resources/
│   ├── messages_en.properties    # English messages
│   ├── messages_vi.properties    # Vietnamese messages
│   └── application.properties    # Configuration
├── src/main/java/vn/hunter/job/
│   ├── config/
│   │   └── MessageSourceConfiguration.java  # i18n Configuration
│   └── controller/
│       └── MessageController.java           # i18n API Endpoints
```

### Configuration

#### MessageSourceConfiguration Class

- Configures `ResourceBundleMessageSource` bean
- Sets locale resolver to use cookies
- Enables language switching via `locale` parameter
- Caches messages for 1 hour

#### Application Properties

```properties
# Locale configuration
spring.web.locale=en_US
spring.web.locale-resolver=fixed
```

### API Endpoints

#### 1. Get Single Message

```
GET /api/v1/messages/{key}
```

Example: `GET /api/v1/messages/validation.name.notblank`

Response:

```json
{
  "key": "validation.name.notblank",
  "message": "Name cannot be blank"
}
```

#### 2. Get All Messages

```
GET /api/v1/messages/all
```

Returns all available message keys and their translations.

Response:

```json
{
  "header.dashboard": "Dashboard",
  "header.company": "Company",
  ...
}
```

#### 3. Get Current Locale

```
GET /api/v1/messages/locale
```

Response:

```json
{
  "locale": "en_US"
}
```

#### 4. Health Check

```
GET /api/v1/messages/health
```

### Using MessageSource in Java Code

#### Injecting MessageSource

```java
@Autowired
private MessageSource messageSource;
```

#### Getting Messages

```java
// In the current locale
String message = messageSource.getMessage("validation.name.notblank", null, LocaleContextHolder.getLocale());

// In a specific locale
String message = messageSource.getMessage("validation.email.notblank", null, new Locale("vi", "VN"));
```

#### Changing Language in Request

Add `locale` parameter to request:

```
GET /api/endpoint?locale=vi
```

### Supported Messages in Backend

**Validation Messages:**

- `validation.name.notblank`
- `validation.email.notblank`
- `validation.email.format`
- `validation.password.notblank`
- `validation.password.size`
- `validation.age.notblank`
- `validation.age.min`
- `validation.age.max`
- `validation.gender.notblank`
- `validation.location.notblank`
- `validation.oldpassword.notblank`
- `validation.newpassword.notblank`
- `validation.newpassword.size`
- `validation.apipath.notblank`
- `validation.method.notblank`
- `validation.module.notblank`

**Response Messages:**

- `response.success`
- `response.error`
- `response.warning`
- `response.info`
- `response.confirmDelete`
- `response.deleteSuccess`
- `response.createSuccess`
- `response.updateSuccess`
- `response.loading`
- `response.noData`
- `response.unauthorized`
- `response.forbidden`
- `response.notFound`
- `response.serverError`

### Adding New Backend Messages

1. **Add to English properties** (`messages_en.properties`):

```properties
validation.newfield.notblank=New field cannot be blank
response.customMessage=This is a custom message
```

2. **Add to Vietnamese properties** (`messages_vi.properties`):

```properties
validation.newfield.notblank=Trường mới không được để trống
response.customMessage=Đây là một thông báo tùy chỉnh
```

3. **Use in your code**:

```java
String message = messageSource.getMessage("validation.newfield.notblank", null, locale);
```

---

## Best Practices

### 1. Frontend

- Always use the `t()` function for UI text
- Keep translation keys organized in clear categories
- Use meaningful key names (e.g., `validation.email.format` instead of `msg1`)
- Add comments in JSON files for complex translations

### 2. Backend

- Use MessageSource for all user-facing messages
- Store message keys in constants to avoid typos
- Use the same key structure in both frontend and backend
- Don't hardcode error messages in exceptions

### 3. Maintenance

- Keep frontend and backend translations in sync
- Document new translation keys
- Test both languages before deployment
- Consider using translation management tools for large projects

---

## Testing

### Frontend Testing

```jsx
import { render, screen } from "@testing-library/react";
import { I18nextProvider } from "react-i18next";
import i18n from "./i18n";

test("should display translated text", () => {
  render(
    <I18nextProvider i18n={i18n}>
      <MyComponent />
    </I18nextProvider>,
  );

  expect(screen.getByText("Dashboard")).toBeInTheDocument();
});
```

### Backend Testing

```java
@Test
public void testMessageSource() {
  String message = messageSource.getMessage("validation.name.notblank", null, Locale.ENGLISH);
  assertEquals("Name cannot be blank", message);
}
```

---

## Troubleshooting

### Messages not loading?

1. Check file names and extensions (should be `.properties` and `.json`)
2. Verify file encoding is UTF-8
3. Check console for error messages
4. Reload page/restart server

### Language not switching?

1. Check browser localStorage for `locale` key
2. Verify language code is correct ('en' or 'vi')
3. Clear browser cache and try again
4. Check LanguageSwitcher component is rendered

### Different text in frontend and backend?

1. Use same message keys in both
2. Check spelling and case sensitivity
3. Verify translations are complete

---

## Future Enhancements

- Add more languages (Chinese, Japanese, etc.)
- Implement server-side rendering with i18n
- Add right-to-left (RTL) language support
- Database-driven translations for easy management
- Translation management UI in admin panel
