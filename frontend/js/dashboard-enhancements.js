const originalFetch = window.fetch.bind(window);
let latestTransactions = [];
const formatMoney = value => new Intl.NumberFormat('vi-VN', {style:'currency', currency:'VND', maximumFractionDigits:0}).format(Number(value || 0));
const formatTime = value => value ? new Date(value).toLocaleString('vi-VN') : '';

function enhanceTransactions() {
  const card = [...document.querySelectorAll('.card')].find(item => /Giao\s*(dịch|dá»‹ch)/i.test(item.querySelector('h2')?.textContent || ''));
  if (!card || !latestTransactions.length) return;
  const signature = latestTransactions.map(item => `${item.id}:${item.status}:${item.amount}:${item.paidAt || ''}`).join('|');
  if (card.dataset.transactionSignature === signature) return;
  card.dataset.transactionSignature = signature;
  [...card.querySelectorAll('.list-row')].forEach((row, index) => {
    const payment = latestTransactions[index];
    if (!payment) return;
    const left = row.querySelector('span');
    const paid = String(payment.status || '').toUpperCase() === 'PAID';
    if (left) {
      const amount = formatMoney(payment.amount);
      const time = paid && payment.paidAt ? ` · ${formatTime(payment.paidAt)}` : paid ? '' : ' · Chưa thanh toán';
      left.insertAdjacentHTML('beforeend', `<br><small>${amount}${time}</small>`);
    }
  });
}

window.fetch = async (...args) => {
  const response = await originalFetch(...args);
  const url = typeof args[0] === 'string' ? args[0] : args[0]?.url || '';
  if (url.includes('/dashboard/')) response.clone().json().then(payload => {
    latestTransactions = payload?.data?.recentTransactions || payload?.data?.recentPayments || [];
    setTimeout(enhanceTransactions, 50);
  }).catch(() => {});
  return response;
};

new MutationObserver(enhanceTransactions).observe(document.body, {childList:true, subtree:true});
