const cartItems = [
    { 
      id: 1, 
      name: "Modern Coffee Table", 
      price: 149.99, 
      quantity: 1, 
      image: "https://ik.imagekit.io/2xkwa8s1i/img/npl_modified_images/WMCF_Image/WMCFTFRAPPER2/WMCFTFRAPPER2_1.jpg?tr=w-1200" 
    },
    { 
      id: 2, 
      name: "Premium Leather Sofa", 
      price: 499.99, 
      quantity: 1, 
      image: "https://www.estre.in/cdn/shop/files/1-min_fc2e5569-9bfc-4765-9cff-9f0a0cfa719f.png?v=1734170209" 
    },
  ];

  const validVouchers = {
    DISCOUNT10: 0.1, // 10% discount
    SAVE20: 0.2, // 20% discount
  };

  let discount = 0;

  function applyVoucher() {
    const voucherInput = document.getElementById('voucher-code');
    const voucherMessage = document.getElementById('voucher-message');
    const voucherCode = voucherInput.value.trim().toUpperCase();

    if (validVouchers[voucherCode]) {
      discount = validVouchers[voucherCode];
      voucherMessage.textContent = `Voucher applied: ${voucherCode} (${discount * 100}% off)`;
      voucherMessage.style.display = 'block';
      voucherMessage.style.color = 'green';
      renderCart();
    } else {
      voucherMessage.textContent = 'Invalid voucher code!';
      voucherMessage.style.display = 'block';
      voucherMessage.style.color = 'red';
    }
  }

  function renderCart() {
    const cartItemsContainer = document.getElementById('cart-items');
    const cartTotal = document.getElementById('cart-total');
    let total = 0;

    cartItemsContainer.innerHTML = '';
    cartItems.forEach(item => {
      total += item.price * item.quantity;

      const cartItem = document.createElement('div');
      cartItem.className = 'cart-item';
      cartItem.innerHTML = `
        <img src="${item.image}" alt="${item.name}">
        <div class="cart-item-details">
          <p>${item.name}</p>
          <p>€${item.price.toFixed(2)}</p>
        </div>
        <div class="cart-item-actions">
          <div class="cart-item-quantity">
            <button onclick="changeQuantity(${item.id}, -1)" ${item.quantity === 1 ? 'disabled' : ''}>-</button>
            <span>${item.quantity}</span>
            <button onclick="changeQuantity(${item.id}, 1)">+</button>
          </div>
          <button class="remove-btn" onclick="removeItem(${item.id})">Remove</button>
        </div>
      `;
      cartItemsContainer.appendChild(cartItem);
    });

    if (discount > 0) {
      total = total - total * discount;
    }

    cartTotal.textContent = `€${total.toFixed(2)}`;
  }

  function changeQuantity(id, change) {
    const item = cartItems.find(item => item.id === id);
    if (item) {
      item.quantity += change;
      renderCart();
    }
  }

  function removeItem(id) {
    const index = cartItems.findIndex(item => item.id === id);
    if (index !== -1) {
      cartItems.splice(index, 1);
      renderCart();
    }
  }

  renderCart();