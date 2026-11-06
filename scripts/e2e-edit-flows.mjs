import { chromium } from 'playwright-core';

const browser = await chromium.launch({ executablePath:'C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe', headless:true });
const page = await browser.newPage();
page.setDefaultTimeout(15_000);
const apiErrors = [];
page.on('response', response => {
  if (response.url().includes('/api/') && response.status() >= 400) apiErrors.push(`${response.status()} ${response.request().method()} ${response.url()}`);
});

async function openEdit(route, query) {
  await page.goto(`http://127.0.0.1:5173/index.html#/${route}`);
  await page.waitForSelector('#search');
  await page.locator('#search').fill(query);
  await page.waitForFunction(value => document.querySelector('tbody')?.innerText.includes(value), query);
  const row = page.locator('tbody tr').filter({hasText:query}).first();
  await row.locator('[data-edit]').click();
  await page.waitForSelector('#entity-form');
}

async function saveStatus(status) {
  await page.locator('#entity-form [name="status"]').selectOption(status);
  await page.locator('#entity-form button').click();
  await page.waitForSelector('#entity-form', {state:'detached'});
}

try {
  await page.goto('http://127.0.0.1:5173/index.html#/login');
  await page.locator('#login-form input[name="email"]').fill('admin@pilates.com');
  await page.locator('#login-form input[name="password"]').fill('123456');
  await page.locator('#login-form button').click();
  await page.waitForSelector('.app-shell');

  await openEdit('members', 'demo.member14@pilates.com');
  await saveStatus('inactive');
  await openEdit('members', 'demo.member14@pilates.com');
  await saveStatus('active');
  process.stdout.write('MEMBER edit + status + restore: PASS\n');

  await openEdit('employees', 'trainer@pilates.com');
  await saveStatus('inactive');
  await openEdit('employees', 'trainer@pilates.com');
  await saveStatus('active');
  process.stdout.write('EMPLOYEE edit + status + restore: PASS\n');

  if (apiErrors.length) throw new Error(apiErrors.join(' | '));
  process.stdout.write('Edit workflows: PASS\n');
} finally {
  await browser.close();
}
