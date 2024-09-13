//loadSingleProduct
const loadProduct = async () => {

    const parameters = new URLSearchParams(window.location.search);

    if (parameters.has("id")) {

        const productId = parameters.get("id");

        const response = await fetch("LoadSingleProduct?id=" + productId);

        if (response.ok) {

            const json = await response.json();

            const id = json.product.id;

            console.log(json.productList);

            document.getElementById("image1").src = "product-images/" + id + "/image1.png";

            document.getElementById("product-title").innerHTML = json.product.title;
//            document.getElementById("product-published-on").innerHTML = json.product.date_time;
            document.getElementById("product-price").innerHTML = new Intl.NumberFormat().format(json.product.price);

            document.getElementById("author").innerHTML = json.product.author;
            document.getElementById("product-category").innerHTML = json.product.category.name;
            
            document.getElementById("product-description").innerHTML = json.product.description;


            document.getElementById("add-to-cart-main").addEventListener(
                    "click",
                    (e) => {
                addToCart(
                        json.product.id,
                        document.getElementById("addToCartQty").value
                        );
                e.preventDefault();
            }
            );


            let productHtml = document.getElementById("similer-product");
            document.getElementById("similar-product-main").innerHTML = "";

            json.productList.forEach(item => {

                let productClone = productHtml.cloneNode(true);

                productClone.querySelector("#similer-product-image").src = "product-images/" + item.id + "/image1.png";

                productClone.querySelector("#similar-product-a1").href = "single-product.html?id=" + item.id;
                productClone.querySelector("#similar-product-a2").href = "single-product.html?id=" + item.id;

                productClone.querySelector("#similar-title").innerHTML = item.title;
                productClone.querySelector("#similar-storage").innerHTML = item.storage.value;
                productClone.querySelector("#similar-price").innerHTML = new Intl.NumberFormat(
                        "en-US",
                        {
                            minimumFractionDigits: 2
                        }
                ).format(item.price);
                productClone.querySelector("#similar-border-color").style.borderColor = item.color.name;
                productClone.querySelector("#similar-color").style.backgroundColor = item.color.name;

                productClone.querySelector("#similar-add-to-cart").addEventListener(
                        "click",
                        (e) => {
                    addToCart(item.id, 1);
                    e.preventDefault();
                });


                console.log(productClone);

                const similarProductMain = document.getElementById("similar-product-main");
                similarProductMain.appendChild(productClone);



            });

            $('.recent-product-activation').slick({
                infinite: true,
                slidesToShow: 4,
                slidesToScroll: 4,
                arrows: true,
                dots: false,
                prevArrow: '<button class="slide-arrow prev-arrow"><i class="fal fa-long-arrow-left"></i></button>',
                nextArrow: '<button class="slide-arrow next-arrow"><i class="fal fa-long-arrow-right"></i></button>',
                responsive: [{
                        breakpoint: 1199,
                        settings: {
                            slidesToShow: 3,
                            slidesToScroll: 3
                        }
                    },
                    {
                        breakpoint: 991,
                        settings: {
                            slidesToShow: 2,
                            slidesToScroll: 2
                        }
                    },
                    {
                        breakpoint: 479,
                        settings: {
                            slidesToShow: 1,
                            slidesToScroll: 1
                        }
                    }
                ]
            });


        } else {
            window.location = "index.html";
        }

    } else {
        window.location = "index.html";
    }



};

