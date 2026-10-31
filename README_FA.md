# ساخت آنلاین APK برای «همراه من» بدون نصب Android Studio

این بسته شامل نسخه وب برنامه و گردش‌کار GitHub Actions است. با اجرای گردش‌کار، GitHub در فضای ابری APK آزمایشی (debug) می‌سازد. این بسته خودش APK آماده نیست.

## مراحل
1. در github.com وارد حساب شوید یا حساب بسازید.
2. از علامت `+` بالا، `New repository` را بزنید و نام `hamrah-man-android` را انتخاب کنید. مخزن را بسازید.
3. در مخزن، `Add file` → `Upload files` را بزنید.
4. فایل‌های `package.json` و `capacitor.config.json` و پوشه `www` را از این بسته آپلود کنید (محتویات را در ریشه مخزن قرار دهید، نه داخل یک پوشه اضافی). `Commit changes` را بزنید.
5. حالا در مخزن `Add file` → `Create new file` را انتخاب کنید.
6. در نام فایل دقیقاً بنویسید: `.github/workflows/build-apk.yml`
7. متن زیر را کامل در فایل جدید بچسبانید و `Commit changes` را بزنید:

```yaml
name: Build Hamrah Man APK
on:
  workflow_dispatch:
  push:
    branches: [ main ]
permissions:
  contents: read
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout source
        uses: actions/checkout@v4
      - name: Set up Node.js
        uses: actions/setup-node@v4
        with:
          node-version: '22'
      - name: Set up Java
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '21'
      - name: Install dependencies
        run: npm install
      - name: Create Android project
        run: npx cap add android
      - name: Sync web app into Android
        run: npx cap sync android
      - name: Build debug APK
        working-directory: android
        run: ./gradlew assembleDebug
      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: HamrahMan-debug-apk
          path: android/app/build/outputs/apk/debug/app-debug.apk
          if-no-files-found: error
```

8. برو به زبانه `Actions` و گردش‌کار `Build Hamrah Man APK` را باز کن. اگر خودکار اجرا نشده، `Run workflow` را بزن.
9. صبر کن تا اجرا با علامت سبز تمام شود. وارد اجرای موفق شو و پایین صفحه در بخش `Artifacts` روی `HamrahMan-debug-apk` بزن تا ZIP دانلود شود.
10. ZIP دانلودشده را باز کن؛ فایل `app-debug.apk` داخل آن است. آن را به گوشی اندروید منتقل و باز کن. اگر گوشی درخواست داد، اجازه نصب برنامه از همان منبع را بده.

## نکته‌ها
- برای نصب شخصی، debug APK مناسب است؛ این فایل نسخه انتشار فروشگاهی/امضاشده نیست.
- این نسخه یک پوسته اندرویدی برای نسخه وب موجود است. اطلاعات در حافظه محلی برنامه ذخیره می‌شود؛ حذف برنامه یا پاک‌کردن داده‌هایش ممکن است اطلاعات محلی را حذف کند. قبل از واردکردن اطلاعات مهم، از خروجی پشتیبان برنامه استفاده کن.
- اگر ساخت شکست خورد، وارد اجرای ناموفق در Actions شو و متن خطای قرمز را بفرست تا علت را بررسی کنیم.
