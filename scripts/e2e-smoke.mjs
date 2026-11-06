import { chromium } from 'playwright-core';

const executablePath = 'C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe';
const baseUrl = process.env.FRONTEND_URL || 'http://127.0.0.1:5173';
const password = process.env.DEMO_PASSWORD || '123456';

const allScenarios = [
  { role: 'ADMIN', email: 'admin@pilates.com', routes: ['', 'members', 'employees', 'packages', 'classes', 'registrations', 'attendance', 'payments', 'reports', 'profile'] },
  { role: 'STAFF', email: 'staff@pilates.com', routes: ['', 'members', 'packages', 'classes', 'registrations', 'attendance', 'payments', 'profile'] },
  { role: 'TRAINER', email: 'trainer@pilates.com', routes: ['', 'classes', 'students', 'attendance', 'history', 'profile'] },
  { role: 'MEMBER', email: 'member@pilates.com', routes: ['', 'packages', 'classes', 'registrations', 'history', 'payments', 'profile'] },
];
const scenarios = process.env.E2E_ROLE
  ? allScenarios.filter(item => item.role === process.env.E2E_ROLE.toUpperCase())
  : allScenarios;

const browser = await chromium.launch({ executablePath, headless: true });
const failures = [];

try {
  for (const scenario of scenarios) {
    const context = await browser.newContext();
    const page = await context.newPage();
    page.setDefaultTimeout(15_000);
    const errors = [];
    let stage = 'khởi tạo';

    page.on('console', message => {
      if (message.type() === 'error' && !message.text().includes('Failed to load resource')) {
        errors.push(`console: ${message.text()}`);
      }
    });
    page.on('pageerror', error => errors.push(`page: ${error.message}`));
    page.on('requestfailed', request => {
      if (request.url().includes('/api/')) errors.push(`request: ${request.method()} ${request.url()} ${request.failure()?.errorText || ''}`);
    });
    page.on('response', response => {
      if (response.url().includes('/api/') && response.status() >= 400) {
        errors.push(`response: ${response.status()} ${response.request().method()} ${response.url()}`);
      }
    });

    try {
      stage = 'mở trang đăng nhập';
      await page.goto(`${baseUrl}/index.html#/login`, { waitUntil: 'networkidle' });
      await page.locator('#login-form input[name="email"]').fill(scenario.email);
      await page.locator('#login-form input[name="password"]').fill(password);
      await page.locator('#login-form button').click();
      await page.waitForFunction(() => location.hash === '#/' && document.querySelector('.app-shell'));

      for (const route of scenario.routes) {
        stage = `route #/${route}`;
        await page.evaluate(value => { location.hash = value ? `#/${value}` : '#/'; }, route);
        await page.waitForTimeout(150);
        await page.waitForFunction(() => {
          const node = document.querySelector('#page');
          return node && node.textContent.trim() && !node.textContent.includes('Đang tải');
        });
        const text = await page.locator('#page').innerText();
        if (/Failed to fetch|Internal Server Error|Vui lòng đăng nhập|không hợp lệ/i.test(text)) {
          throw new Error(`route #/${route}: ${text.slice(0, 180)}`);
        }
      }

      await page.evaluate(() => { location.hash = '#/profile'; });
      stage = 'mở form hồ sơ';
      await page.waitForTimeout(150);
      const editProfile = page.locator('#edit-profile');
      if (await editProfile.count()) {
        await editProfile.click();
        await page.locator('#profile-form').waitFor();
        await page.locator('[data-close]').first().click();
      }

      if (scenario.role === 'ADMIN') {
        stage = 'mở form tạo hội viên';
        await page.evaluate(() => { location.hash = '#/members'; });
        await page.waitForTimeout(150);
        await page.locator('#add').click();
        await page.locator('#entity-form').waitFor();
        if (!(await page.locator('#entity-form input[name="password"]').count())) {
          throw new Error('Form tạo hội viên thiếu trường mật khẩu ban đầu');
        }
        await page.locator('[data-close]').first().click();
      }

      if (errors.length) throw new Error(errors.join(' | '));
      process.stdout.write(`${scenario.role}: PASS (${scenario.routes.length} routes)\n`);
    } catch (error) {
      failures.push(`${scenario.role} [${stage}]: ${error.message}${errors.length ? ` | ${errors.join(' | ')}` : ''}`);
      process.stdout.write(`${scenario.role}: FAIL\n`);
    } finally {
      await context.close();
    }
  }
} finally {
  await browser.close();
}

if (failures.length) {
  process.stderr.write(`${failures.join('\n')}\n`);
  process.exit(1);
}

process.stdout.write('E2E browser smoke test: PASS\n');
