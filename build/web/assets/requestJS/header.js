const LoadUserHeaderFeatures = async () => {

    const response = await fetch("LoadUserHeader");


    if (response.ok) {
        const json = await response.json();

        console.log(json);


    } else {
        console.log("Something went wrong");

    }


};
