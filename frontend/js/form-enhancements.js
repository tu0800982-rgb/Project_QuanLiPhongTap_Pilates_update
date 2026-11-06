const observer = new MutationObserver(() => {
  const form = document.querySelector('#entity-form');
  if (!form || form.dataset.enhanced === 'true') return;
  form.dataset.enhanced = 'true';
  const route = location.hash.replace(/^#\/?/, '').split('/')[0];
  const modalTitle = form.closest('.modal')?.querySelector('.modal-head h2')?.textContent;
  if (!['members', 'employees'].includes(route) || modalTitle !== 'Thêm mới') return;
  const label = document.createElement('label');
  label.innerHTML = '<span class="label">Mật khẩu ban đầu</span><input class="field" name="password" type="password" minlength="6" maxlength="72" autocomplete="new-password" required>';
  form.insertBefore(label, form.querySelector('button[type="submit"], button'));
});

observer.observe(document.body, {childList:true, subtree:true});
