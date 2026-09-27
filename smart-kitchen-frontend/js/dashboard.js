const LATEST_API = "http://localhost:8080/api/sensor-data/latest";
const HISTORY_API = "http://localhost:8080/api/sensor-data/history/KITCHEN_001";
const AI_API = "http://localhost:8080/api/ai/suggestion/latest";

const token = localStorage.getItem("token");

if (!token) {
    window.location.href = "login.html";
}

let gasChart;
let tempChart;
let currentChart;

// ---------------- FETCH LATEST SENSOR DATA ----------------

async function fetchLatestData() {
    try {

        const response = await fetch(LATEST_API, {
            method: "GET",
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        if (response.status === 401 || response.status === 403) {
            logout();
            return;
        }

        const data = await response.json();

        if (!data) {
            console.log("No latest data found");
            return;
        }

        // Sensor values
        document.getElementById("gasValue").innerText = data.gasValue;

        document.getElementById("currentValue").innerText =
            data.currentValue;

        document.getElementById("temperature").innerText =
            data.temperature + " °C";

        // Flame logic
        document.getElementById("flameValue").innerText =
            data.flameValue === 1
                ? "Flame Detected"
                : "No Flame";

        // Risk + Alert
        document.getElementById("riskLevel").innerText =
            data.riskLevel;

        document.getElementById("alertMessage").innerText =
            data.alertMessage;

        // Time
        document.getElementById("createdAt").innerText =
            formatDateTime(data.createdAt);

        // Change card color
        updateStatusColor(data.riskLevel);

    } catch (error) {
        console.error("Latest data fetch error:", error);
    }
}

// ---------------- FETCH AI SUGGESTION ----------------

async function fetchAiSuggestion() {

    try {

        const response = await fetch(AI_API, {
            method: "GET",
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        if (response.status === 401 || response.status === 403) {
            logout();
            return;
        }

        const data = await response.json();

        document.getElementById("aiSuggestion").innerText =
            data.suggestion || "AI suggestion unavailable.";

    } catch (error) {

        console.error("AI suggestion error:", error);

        document.getElementById("aiSuggestion").innerText =
            "AI suggestion unavailable.";
    }
}

// ---------------- FETCH HISTORY DATA ----------------

async function fetchHistoryData() {

    try {

        const response = await fetch(HISTORY_API, {
            method: "GET",
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        if (response.status === 401 || response.status === 403) {
            logout();
            return;
        }

        const data = await response.json();

        if (!data || data.length === 0) {
            console.log("No history data found");
            return;
        }

        // Last 10 records
        const latest10 = data.slice(0, 10).reverse();

        const labels =
            latest10.map(item => formatTimeOnly(item.createdAt));

        const gasValues =
            latest10.map(item => item.gasValue);

        const tempValues =
            latest10.map(item => item.temperature);

        const currentValues =
            latest10.map(item => item.currentValue);

        updateCharts(
            labels,
            gasValues,
            tempValues,
            currentValues
        );

    } catch (error) {
        console.error("History data fetch error:", error);
    }
}

// ---------------- CREATE CHART ----------------

function createChart(canvasId, label, labels, values) {

    const ctx = document.getElementById(canvasId);

    return new Chart(ctx, {

        type: "line",

        data: {
            labels: labels,

            datasets: [{
                label: label,
                data: values,
                borderWidth: 2,
                tension: 0.3,
                fill: false
            }]
        },

        options: {
            responsive: true,

            animation: false,

            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });
}

// ---------------- UPDATE CHARTS ----------------

function updateCharts(
    labels,
    gasValues,
    tempValues,
    currentValues
) {

    if (!gasChart) {

        gasChart = createChart(
            "gasChart",
            "Gas Value",
            labels,
            gasValues
        );

        tempChart = createChart(
            "tempChart",
            "Temperature °C",
            labels,
            tempValues
        );

        currentChart = createChart(
            "currentChart",
            "Current Value",
            labels,
            currentValues
        );

    } else {

        // Gas chart
        gasChart.data.labels = labels;
        gasChart.data.datasets[0].data = gasValues;
        gasChart.update();

        // Temperature chart
        tempChart.data.labels = labels;
        tempChart.data.datasets[0].data = tempValues;
        tempChart.update();

        // Current chart
        currentChart.data.labels = labels;
        currentChart.data.datasets[0].data = currentValues;
        currentChart.update();
    }
}

// ---------------- STATUS CARD COLOR ----------------

function updateStatusColor(riskLevel) {

    const statusCard =
        document.querySelector(".status-card");

    statusCard.classList.remove(
        "safe",
        "medium",
        "high",
        "critical"
    );

    if (riskLevel === "SAFE") {

        statusCard.classList.add("safe");

    } else if (riskLevel === "MEDIUM") {

        statusCard.classList.add("medium");

    } else if (riskLevel === "HIGH") {

        statusCard.classList.add("high");

    } else if (riskLevel === "CRITICAL") {

        statusCard.classList.add("critical");
    }
}

// ---------------- FORMAT DATE ----------------

function formatDateTime(dateTime) {

    if (!dateTime) return "--";

    return new Date(dateTime).toLocaleString();
}

// ---------------- FORMAT TIME ----------------

function formatTimeOnly(dateTime) {

    if (!dateTime) return "--";

    return new Date(dateTime).toLocaleTimeString();
}

// ---------------- LOGOUT ----------------

function logout() {

    localStorage.removeItem("token");

    window.location.href = "login.html";
}

// ---------------- INITIAL LOAD ----------------

fetchLatestData();

fetchHistoryData();

fetchAiSuggestion();

// ---------------- AUTO REFRESH ----------------

setInterval(() => {

    fetchLatestData();

    fetchHistoryData();

    fetchAiSuggestion();

}, 10000);