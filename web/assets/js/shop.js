document.addEventListener('DOMContentLoaded', () => {
    const slider = document.querySelector('.slider');
    const sliderTrack = document.querySelector('.slider-track');
    const sliderRange = document.querySelector('.slider-range');
    const handleMin = document.getElementById('handleMin');
    const handleMax = document.getElementById('handleMax');
    const minValue = document.getElementById('minValue');
    const maxValue = document.getElementById('maxValue');

    const MIN_VALUE = 0;
    const MAX_VALUE = 7500;
    const sliderWidth = slider.offsetWidth;

    // Initialize handle positions
    let minHandlePos = MIN_VALUE;
    let maxHandlePos = MAX_VALUE;

    function updateSlider() {
        // Calculate positions as percentages
        const minPos = (minHandlePos / MAX_VALUE) * 100;
        const maxPos = (maxHandlePos / MAX_VALUE) * 100;

        // Update slider range and handle positions
        sliderRange.style.left = `${minPos}%`;
        sliderRange.style.width = `${maxPos - minPos}%`;

        handleMin.style.left = `${minPos}%`;
        handleMax.style.left = `${maxPos}%`;

        // Update displayed values
        minValue.textContent = minHandlePos;
        maxValue.textContent = maxHandlePos;
    }

    function onHandleMouseDown(e, handleType) {
        e.preventDefault();

        function onMouseMove(e) {
            const rect = slider.getBoundingClientRect();
            let newPos = e.clientX - rect.left;

            newPos = Math.max(0, Math.min(newPos, sliderWidth));
            const newValue = Math.round((newPos / sliderWidth) * MAX_VALUE);

            if (handleType === 'min') {
                if (newValue > maxHandlePos) {
                    newValue = maxHandlePos;
                }
                minHandlePos = newValue;
            } else {
                if (newValue < minHandlePos) {
                    newValue = minHandlePos;
                }
                maxHandlePos = newValue;
            }

            updateSlider();
        }

        function onMouseUp() {
            document.removeEventListener('mousemove', onMouseMove);
            document.removeEventListener('mouseup', onMouseUp);
        }

        document.addEventListener('mousemove', onMouseMove);
        document.addEventListener('mouseup', onMouseUp);
    }

    handleMin.addEventListener('mousedown', (e) => onHandleMouseDown(e, 'min'));
    handleMax.addEventListener('mousedown', (e) => onHandleMouseDown(e, 'max'));

    // Initialize slider position and values
    updateSlider();
});
