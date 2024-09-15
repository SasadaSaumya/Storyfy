var shopProduct = document.getElementById("product-card");
var currentPage = 0;
var paginationButton = document.getElementById("pagination-item");

const reset = () => {
    window.location.reload();
};

const loadProductFeatures = async () => {
    const popup = Notification();

    const parameters = new URLSearchParams(window.location.search);
    if (parameters.has("category")) {

        const categoryNameUrl = parameters.get("category");
    }

    try {
        const response = await fetch("LoadAllProducts");
        if (response.ok) {
            const json = await response.json();
            console.log(json);

            // Load category, author, and publisher options
            loadOption("category", json.categoryList);
            loadOption("authors", json.authorList);
            loadOption("publisher", json.publisherList);

            // Load products
            updateProductCard(json);
        } else {
            popup.error({
                title: 'Error',
                message: "Something went wrong"
            });
        }
    } catch (error) {
        popup.error({
            title: 'Error',
            message: "Network error"
        });
    }
};

const loadOption = (prefix, dataList) => {
    let main = document.getElementById(prefix + "-main");
    let sub = document.getElementById(prefix + "-sub");

    main.innerHTML = "";

    dataList.forEach(data => {
        let sub_clone = sub.cloneNode(true);
        sub_clone.querySelector("#" + prefix + "-text").innerHTML = data.name;
        main.appendChild(sub_clone);
    });
};

const updateProductCard = (json) => {
    let shopProductContainer = document.getElementById("product-container");
    shopProductContainer.innerHTML = "";

    json.productList.forEach(product => {
        let shopProductClone = shopProduct.cloneNode(true);
        shopProductClone.querySelector("#product-image").src = "product-images/" + product.id + "/image1.png";
        shopProductClone.querySelector("#product-title-1").innerHTML = product.title;
        shopProductClone.querySelector("#product-image-href").href = "product.html?id=" + product.id;
        shopProductClone.querySelector("#product-price").innerHTML = new Intl.NumberFormat("en-US", {
            minimumFractionDigits: 2
        }).format(product.price);

        shopProductContainer.appendChild(shopProductClone);
    });

    // Pagination
    let paginationContainer = document.getElementById("pagination-container");
    paginationContainer.innerHTML = "";

    let productCount = json.allProductCount;
    const prePageProductCount = 6;
    let pages = Math.ceil(productCount / prePageProductCount);

    if (currentPage != 0) {
        let shoppaginationButtonClonePrev = paginationButton.cloneNode(true);
        shoppaginationButtonClonePrev.innerHTML = "Prev";
        shoppaginationButtonClonePrev.addEventListener("click", () => {
            currentPage--;
            searchProduct(currentPage * prePageProductCount);
        });
        paginationContainer.appendChild(shoppaginationButtonClonePrev);
    }

    for (let i = 0; i < pages; i++) {
        let shoppaginationButtonClone = paginationButton.cloneNode(true);
        shoppaginationButtonClone.innerHTML = i + 1;
        shoppaginationButtonClone.addEventListener("click", () => {
            currentPage = i;
            searchProduct(i * prePageProductCount);
        });

        shoppaginationButtonClone.className = i === currentPage ? "btn-info  ml--10" : "btn-outline-info  ml--10";
        paginationContainer.appendChild(shoppaginationButtonClone);
    }

    if (currentPage != (pages - 1)) {
        let shoppaginationButtonCloneNext = paginationButton.cloneNode(true);
        shoppaginationButtonCloneNext.innerHTML = "Next";
        shoppaginationButtonCloneNext.addEventListener("click", () => {
            currentPage++;
            searchProduct(currentPage * prePageProductCount);
        });
        paginationContainer.appendChild(shoppaginationButtonCloneNext);
    }
};

const searchProduct = async (firstResult) => {
    const popup = Notification();

    function getCheckedRadioText(inputName) {
        const radios = document.querySelectorAll(`input[name="${inputName}"]`);
        for (const radio of radios) {
            if (radio.checked) {
                const container = radio.closest('.col-12');
                const link = container ? container.querySelector('a') : null;
                return link ? link.textContent : '';
            }
        }
        return '';
    }

    let categoryName = getCheckedRadioText("radio-category");
    let authorName = getCheckedRadioText("radio-authors");
    let publisherName = getCheckedRadioText("radio-publisher");

    let priceRangeStart = parseFloat(document.getElementById("minValue").innerHTML) || 0;
    let priceRangeEnd = parseFloat(document.getElementById("maxValue").innerHTML) || Number.MAX_VALUE;
    let sortText = document.getElementById("sortby").value;

    const data = {
        firstResult: firstResult,
        categoryName: categoryName,
        publisherName: publisherName,
        authorName: authorName,
        priceRangeStart: priceRangeStart,
        priceRangeEnd: priceRangeEnd,
        sortText: sortText
    };

    try {
        const response = await fetch("SearchProducts", {
            method: "POST",
            body: JSON.stringify(data),
            headers: {
                "Content-Type": "application/json"
            }
        });

        if (response.ok) {
            const json = await response.json();
            if (json.success) {
                updateProductCard(json);
            } else {
                popup.error({
                    title: 'Error',
                    message: "Error"
                });
            }
        } else {
            popup.error({
                title: 'Error',
                message: "Error"
            });
        }
    } catch (error) {
        popup.error({
            title: 'Error',
            message: "Network error"
        });
    }
};
