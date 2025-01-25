// Simulated product data (This can be replaced with dynamic data later)
const products = [
    { name: "Sofa", category: "Living Room", price: "$500", image: "sofa.jpg" },
    { name: "Dining Table", category: "Dining", price: "$700", image: "dining.jpg" },
    { name: "Chair", category: "Office", price: "$150", image: "chair.jpg" },
    { name: "Coffee Table", category: "Living Room", price: "$250", image: "coffee_table.jpg" },
    { name: "Bed", category: "Bedroom", price: "$900", image: "bed.jpg" },
    { name: "Cabinet", category: "Storage", price: "$350", image: "cabinet.jpg" }
];

// Function to render products
function renderProducts(filteredProducts) {
    const productList = document.getElementById("product-list");
    productList.innerHTML = '';  // Clear existing products
    filteredProducts.forEach(product => {
        const productDiv = document.createElement("div");
        productDiv.className = "product";
        productDiv.innerHTML = `
            <img src="${product.image}" alt="${product.name}">
            <h3>${product.name}</h3>
            <p>${product.category}</p>
            <span>${product.price}</span>
        `;
        productList.appendChild(productDiv);
    });
}

// Function to search products by category
function searchProducts() {
    const searchTerm = document.getElementById("search-bar").value.toLowerCase();
    const filteredProducts = products.filter(product => product.category.toLowerCase().includes(searchTerm));
    renderProducts(filteredProducts);
}

// Initial render of all products
renderProducts(products);
