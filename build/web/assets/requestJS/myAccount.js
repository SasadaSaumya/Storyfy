//load add product nav => product features 

const loadProductFeatures = async () => {

    const response = await fetch("LoadProductFeatures");


    if (response.ok) {
        const json = response.json();

        console.log(json);

    } else {
        console.log("Something went wrong");

    }


};