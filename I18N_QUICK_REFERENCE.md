# I18N Quick Reference Guide

## Frontend - React

### Import Translation Hook

```jsx
import { useTranslation } from "react-i18next";
```

### Use in Component

```jsx
const { t, i18n } = useTranslation();

// Display translated text
<h1>{t("header.dashboard")}</h1>;

// Change language
i18n.changeLanguage("vi"); // Vietnamese
i18n.changeLanguage("en"); // English

// Get current language
console.log(i18n.language); // 'en' or 'vi'
```

### Language Switcher (Already Integrated)

The language switcher is available in header. Users can click to switch between English and Vietnamese.

---

## Backend - Spring Boot

### Inject MessageSource

```java
@Autowired
private MessageSource messageSource;
```

### Get Message

```java
String message = messageSource.getMessage(
  "validation.name.notblank",
  null,
  LocaleContextHolder.getLocale()
);
```

### Change Language via Request

```
GET /api/endpoint?locale=vi
```

### Message API Endpoints

- `GET /api/v1/messages/{key}` - Get single message
- `GET /api/v1/messages/all` - Get all messages
- `GET /api/v1/messages/locale` - Get current locale
- `GET /api/v1/messages/health` - Health check

---

## Common Translation Keys

### Headers & Navigation

- `header.dashboard`
- `header.company`
- `header.job`
- `header.user`
- `header.logout`

### Forms

- `form.name`
- `form.email`
- `form.password`
- `form.submitButton`
- `form.cancelButton`

### Validation

- `validation.name.notblank`
- `validation.email.notblank`
- `validation.password.notblank`
- `validation.age.min`

### Messages

- `message.success`
- `message.error`
- `message.deleteSuccess`
- `message.createSuccess`
- `message.updateSuccess`

---

## Add New Translation

### 1. Frontend (JSON)

```json
// public/locales/en/common.json
{
  "myKey": "English text"
}

// public/locales/vi/common.json
{
  "myKey": "Văn bản tiếng Việt"
}
```

### 2. Backend (Properties)

```properties
# messages_en.properties
mykey=English text

# messages_vi.properties
mykey=Văn bản tiếng Việt
```

---

## Language Codes

- `en` - English
- `vi` - Vietnamese (Tiếng Việt)

---

## Files Modified/Created

### Frontend

- `src/i18n.js` - i18n configuration
- `src/components/LanguageSwitcher.jsx` - Language switcher component
- `public/locales/en/common.json` - English translations
- `public/locales/vi/common.json` - Vietnamese translations
- `src/main.jsx` - Updated to load i18n
- `src/components/admin/layout.admin.jsx` - Added language switcher
- `src/components/client/layout/header.client.jsx` - Added language switcher

### Backend

- `src/main/java/vn/hunter/job/config/MessageSourceConfiguration.java` - i18n config
- `src/main/java/vn/hunter/job/controller/MessageController.java` - Message API
- `src/main/resources/messages_en.properties` - English messages
- `src/main/resources/messages_vi.properties` - Vietnamese messages
- `src/main/resources/application.properties` - Added i18n properties

---

## Storage & Persistence

- Frontend: Uses browser localStorage with key `locale`
- Backend: Uses HTTP cookies with name `locale`
- Both default to English if no preference is set

---

## Need Help?

See `I18N_GUIDE.md` for detailed documentation.
