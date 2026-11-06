const labels = {
  employeeType:'Loại nhân sự', employeeCode:'Mã nhân viên', packageCode:'Mã gói tập', fullName:'Họ và tên', code:'Mã',
  classType:'Loại lớp', classTypeId:'Mã loại lớp', transactionCode:'Mã giao dịch',
  packageName:'Tên gói tập', specialization:'Chuyên môn', biography:'Giới thiệu',
  ADMIN:'Quản trị viên', STAFF:'Nhân viên', TRAINER:'Huấn luyện viên', MEMBER:'Hội viên',
  ACTIVE:'Đang hoạt động', INACTIVE:'Ngừng hoạt động', OPEN:'Đang mở', CLOSED:'Đã đóng',
  CANCELLED:'Đã hủy', COMPLETED:'Đã hoàn thành', PAID:'Đã thanh toán', PENDING:'Đang chờ',
  REFUNDED:'Đã hoàn tiền', BANK_TRANSFER:'Chuyển khoản', CASH:'Tiền mặt', CARD:'Thẻ',
  PRESENT:'Có mặt', ABSENT:'Vắng mặt', EXCUSED:'Có phép'
};

function translate(root=document.body) {
  const walker=document.createTreeWalker(root,NodeFilter.SHOW_TEXT);
  const nodes=[];
  while(walker.nextNode()) nodes.push(walker.currentNode);
  nodes.forEach(node=>{
    if(node.parentElement?.matches('input,textarea,select,option,script,style')) return;
    const value=node.nodeValue.trim();
    if(labels[value]) node.nodeValue=node.nodeValue.replace(value,labels[value]);
  });
}

new MutationObserver(()=>translate()).observe(document.body,{childList:true,subtree:true});
translate();
