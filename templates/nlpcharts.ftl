<!DOCTYPE html>
<html lang="DE">
<head>
    <title>
        NLP-Charts
    </title>
    <meta charset="UTF-8">
    <script src="https://kit.fontawesome.com/fc91097d0e.js" crossorigin="anonymous"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
    <script src="/static/charts.js"></script>
    <script src="https://d3js.org/d3.v7.min.js"></script>
    <style>
        .header-div {
            display: flex;
            align-items: center;
            justify-content: center;
            width: 100%;
            height: 40px;
            margin-top: 0;
            margin-bottom: 20px
        }
        .header {
            font-size: 20px;
            padding: 0;
            margin: 0;
        }
        table {
            width: 90%;
            border-collapse: collapse;
        }
        tr {
            border-bottom: 1px solid gray;
            padding: 10px;
        }
        td {
            text-align: center;
        }
        tr:hover {
            background-color: cornsilk;
        }
        .table-header-row:hover {
            background-color: white;
        }
        a {
            color: black;
            cursor: pointer;
        }
        .content-wrapper {
            display: flex;
            flex-direction: row;
            width: 100%;
        }
        .speechlist-section-wrapper {
            width: 100%;
            height: 100%;
            display: flex;
            flex-direction: row;
            justify-content: center;
            align-items: flex-start;
        }
        .speechlist-section {
            width: 25%;
            border-right: 1px solid gray;
        }
        .table-container {
            max-height: 700px;
            overflow-y: auto;
            width: 100%;
        }
        .date-filter-wrapper, .fraction-filter-wrapper, .speaker-filter-wrapper {
            max-height: 250px;
            overflow-y: auto;
            width: 100%;
        }
        .table-caption {
            display: flex;
            justify-content: center;
            position: sticky;
            top: 0;
            background: white;
            padding: 5px;
            font-weight: bold;
            z-index: 10;
        }
        .table-caption:hover {
            cursor: pointer;
        }
        .filter-tablebody {
            display: none;
        }
        .fa-square:hover, .fa-check-square:hover {
            cursor: pointer;
        }
        .charts-wrapper {
            width: 50%;
        }
        .filter-wrapper {
            width: 25%;
            border-left: 1px solid gray;
        }
        .spinner {
            display: flex;
            align-items: center;
            justify-content: center;
            flex-direction: column;
            height: 150px;
        }
        .spinner i {
            font-size: 48px;
        }
        .submit-wrapper {
            width: 100%;
            display: flex;
            justify-content: center;
            align-items: center;
        }
        .submit-button {
            border: none;
            background-color: green;
            color: white;
            border-radius: 5px;
            font-size: 1rem;
            cursor: pointer;
            box-shadow: 1px 1px gray;
            margin-right: 10px;
            width: 33%;
        }
        .reset-button {
            border: none;
            background-color: red;
            color: white;
            border-radius: 5px;
            font-size: 1rem;
            cursor: pointer;
            box-shadow: 1px 1px gray;
            width: 32%;
        }
        .select {
            border-radius: 5px;
            border: none;
            box-shadow: 1px 1px gray;
            margin-bottom: 10px;
            font-size: 1rem;
        }
        .speech-select-wrapper {
            padding-top: 1rem;
            padding-bottom: 2rem;
        }
        .charts-content-wrapper {
            overflow-y: auto;
            max-height: 700px;
        }
        .bar-chart-wrapper, .bubble-chart-wrapper, .sunburst-chart-wrapper, .radar-chart-wrapper {
            display: none;
            justify-content: flex-start;
            align-items: center;
            width: 100%;
            overflow-y: auto;
            overflow-x: auto;
            border-bottom: 1px solid gray;
        }
        .no-speeches-found {
            display: flex;
            flex-direction: column;
            justify-content: flex-start;
            align-items: center;
            color: red;
            font-size: 1.5rem;
            font-weight: bold;
            margin-top: 40px;
        }
    </style>
</head>
<body>
<div>
    <div class="content-wrapper">
        <div class="speechlist-section">
            <div class="speechlist-section-wrapper">
                <div class="speechlist-wrapper">
                    <div class="speech-select-wrapper">
                        <label for="speech-select">Wählen Sie eine Sitzung: </label>
                        <select class="select" name="speech-select" id="speech-select" onchange="setSession(this.value)">
                            <#list sessions as session>
                                <option value="${session.id}">${session.id}</option>
                            </#list>
                        </select>
                    </div>
                    <div class="table-container">
                        <table id="speeches-table">
                            <caption id="speech-table-caption">Reden für Sitzung 1</caption>
                            <tbody>
                            <tr class="table-header-row">
                                <th>Sitzung</th>
                                <th>Redner</th>
                                <th>Rede-ID</th>
                                <th>Auswahl</th>
                            </tr>
                            <#list speeches as speech>
                                <tr id="${speech.speechId}">
                                    <td><a href="/speeches?id=${speech.sessionId}">${speech.sessionId}</a></td>
                                    <td><a href="/speakers/portfolio?id=${speech.speakerId}">${speech.speaker}</td>
                                    <td><a href="/speeches/speechdetails?id=${speech.speechId}">${speech.speechId}</a></td>
                                    <td onclick="setFilterSpeech('${speech.speechId}')">
                                        <i id="${speech.speechId}-box" class="fa-regular fa-square"></i>
                                    </td>
                                </tr>
                            </#list>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
        <div class="charts-wrapper">
            <div class="header-div">
                <h1 class="header">NLP-Charts</h1>
            </div>
            <div class="submit-wrapper">
                <button class="submit-button" style="background-color: blue" onclick="fetchChartDataForAllSpeeches()">Analyse für alle Reden abrufen</button>
                <button class="submit-button" onclick="fetchChartData()">Gefilterte Analyse abrufen</button>
                <button class="reset-button" onclick="resetAllFilters()">Alle Filter zurücksetzen</button>
            </div>
            <div class="charts-content-wrapper">
                <div id="spinner-wrapper" class="spinner-wrapper"></div>
                <div id="bubble-chart-wrapper" class="bubble-chart-wrapper">
                    <h4>Topics-Verteilung</h4>
                    <svg id="bubble-chart"></svg>
                </div>
                <div id="bar-chart-wrapper" class="bar-chart-wrapper">
                    <h4>POS-Verteilung</h4>
                    <svg id="bar-chart"></svg>
                </div>
                <div id="radar-chart-wrapper" class="radar-chart-wrapper">
                    <h4>Sentiments-Verteilung</h4>
                    <svg id="radar-chart"></svg>
                </div>
                <div id="sunburst-chart-wrapper" class="sunburst-chart-wrapper">
                    <h4>Named-Entities-Verteilung</h4>
                    <svg id="sunburst-chart"></svg>
                </div>
            </div>
        </div>
        <div class="filter-wrapper">
            <h4>Weitere Filtermöglichkeiten (Nicht kompatibel mit dem Rede-Filter)</h4>
            <div class="date-filter-wrapper" id="date-filter-wrapper">
                <div class="table-container">
                    <div id="dates-table-caption" class="table-caption" onclick="showTable('date-filter-wrapper')">Rede-Datum</div>
                    <table id="dates-table">
                        <tbody class="filter-tablebody">
                        <tr class="table-header-row">
                            <th>Datum</th>
                            <th>Auswahl</th>
                        </tr>
                        <#list dates as date>
                            <tr>
                                <td>${date}</td>
                                <td onclick="setFilterDate('${date}')">
                                    <i id="${date}-box" class="fa-regular fa-square"></i>
                                </td>
                            </tr>
                        </#list>
                        </tbody>
                    </table>
                </div>
            </div>
            <div class="fraction-filter-wrapper" id="fraction-filter-wrapper">
                <div class="table-container">
                    <div id="fraction-table-caption" class="table-caption" onclick="showTable('fraction-filter-wrapper')">Fraktionen</div>
                    <table id="fractions-table">
                        <tbody class="filter-tablebody">
                        <tr class="table-header-row">
                            <th>Fraktion</th>
                            <th>Auswahl</th>
                        </tr>
                        <#list fractions as fraction>
                            <tr>
                                <td>${fraction.shortName}</td>
                                <td onclick="setFilterFraction('${fraction.shortName}')">
                                    <i id="${fraction.shortName}-box" class="fa-regular fa-square"></i>
                                </td>
                            </tr>
                        </#list>
                        </tbody>
                    </table>
                </div>
            </div>
            <div class="speaker-filter-wrapper" id="speaker-filter-wrapper">
                <div class="table-container">
                    <div id="speaker-table-caption" class="table-caption" onclick="showTable('speaker-filter-wrapper')">Redner-Liste</div>
                    <table id="speaker-table">
                        <tbody class="filter-tablebody">
                        <tr class="table-header-row">
                            <th>Redner</th>
                            <th>Auswahl</th>
                        </tr>
                        <#list speakersJson as speaker>
                            <tr id="${speaker.id}">
                                <td>${speaker.nameSurname}</td>
                                <td onclick="setFilterSpeaker('${speaker.id}')">
                                    <i id="${speaker.id}-box" class="fa-regular fa-square"></i>
                                </td>
                            </tr>
                        </#list>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
<script>
    let selectedSpeeches = [];
    let selectedDates = [];
    let selectedFractions = [];
    let selectedSpeakers = [];
    const speakerList = ${speakersJsonString};

    function showTable(tableId) {
        const container = document.getElementById(tableId);
        const tbody = container.getElementsByClassName("filter-tablebody")[0];
        const computedDisplay = window.getComputedStyle(tbody).display;

        if (computedDisplay === "none") {
            tbody.style.display = "table-row-group";
        } else {
            tbody.style.display = "none";
        }
    }

    function resetAllFilters() {
        selectedSpeeches = [];
        selectedDates = [];
        selectedSpeakers = [];
        selectedFractions = [];
        const checkboxes = document.getElementsByClassName("fa-check-square");
        [...checkboxes].forEach(checkbox => {
            checkbox.classList.remove("fa-check-square");
            checkbox.classList.add("fa-square");});
    }

    function setFilterSpeaker(speaker) {
        if (selectedSpeeches.length !== 0) {
            selectedSpeeches.forEach((speechId) => {
                const speechCheckbox = document.getElementById(speechId+"-box");
                speechCheckbox.classList.remove("fa-check-square");
                speechCheckbox.classList.add("fa-square");
            })
            selectedSpeeches = [];
        }
        const index = selectedSpeakers.indexOf(speaker);
        const checkbox = document.getElementById(speaker+"-box");

        if (index === -1) {
            selectedSpeakers.push(speaker);
            checkbox.classList.remove("fa-square");
            checkbox.classList.add("fa-check-square");
        } else {
            selectedSpeakers.splice(index, 1);
            checkbox.classList.remove("fa-check-square");
            checkbox.classList.add("fa-square");
        }
    }

    function setFilterFraction(fraction) {
        if (selectedSpeeches.length !== 0) {
            selectedSpeeches.forEach((speechId) => {
                const speechCheckbox = document.getElementById(speechId+"-box");
                speechCheckbox.classList.remove("fa-check-square");
                speechCheckbox.classList.add("fa-square");
            })
            selectedSpeeches = [];
        }
        const index = selectedFractions.indexOf(fraction);
        const checkbox = document.getElementById(fraction+"-box");
        const speakerTable = document.getElementById("speaker-table");

        if (index === -1) {
            selectedFractions.push(fraction);
            checkbox.classList.remove("fa-square");
            checkbox.classList.add("fa-check-square");
        } else {
            selectedSpeakers.forEach((speaker) => {
                speakerCheckbox = document.getElementById(speaker+"-box");
                speakerCheckbox.classList.remove("fa-check-square");
                speakerCheckbox.classList.add("fa-square");
            })
            selectedSpeakers = [];
            selectedFractions.splice(index, 1);
            checkbox.classList.remove("fa-check-square");
            checkbox.classList.add("fa-square");
        }

        let tr = speakerTable.getElementsByTagName("tr");
        if (selectedFractions.length !== 0) {
            for (let i = 1; i < tr.length; i++) {
                let id = tr[i].id;
                for (let speaker of speakerList) {
                    if (speaker.id === id && selectedFractions.indexOf(speaker.fractionName) === -1) {
                        tr[i].style.display = "none";
                    } else if (speaker.id === id && selectedFractions.indexOf(speaker.fractionName) !== -1) {
                        tr[i].style.display = "";
                    }
                }
            }
        } else if (selectedFractions.length === 0) {for (let i = 1; i < tr.length; i++) {tr[i].style.display = "";}}
    }

    function setFilterSpeech(speechId) {
        if (selectedDates.length !== 0) {
            selectedDates.forEach((date) => {
                const dateCheckbox = document.getElementById(date+"-box");
                dateCheckbox.classList.remove("fa-check-square");
                dateCheckbox.classList.add("fa-square");
            })
            selectedDates = [];
        }
        if (selectedSpeakers.length !== 0) {
            selectedSpeakers.forEach((speaker) => {
                const speakerCheckbox = document.getElementById(speaker+"-box");
                speakerCheckbox.classList.remove("fa-check-square");
                speakerCheckbox.classList.add("fa-square");
            })
            selectedSpeakers = [];
        }
        if (selectedFractions.length !== 0) {
            selectedFractions.forEach((fraction) => {
                const fractionCheckbox = document.getElementById(fraction+"-box");
                fractionCheckbox.classList.remove("fa-check-square");
                fractionCheckbox.classList.add("fa-square");
            })
            selectedFractions = [];
        }
        const index = selectedSpeeches.indexOf(speechId);
        const checkbox = document.getElementById(speechId+"-box");

        if (index === -1) {
            selectedSpeeches.push(speechId);
            checkbox.classList.remove("fa-square");
            checkbox.classList.add("fa-check-square");
        } else {
            selectedSpeeches.splice(index, 1);
            checkbox.classList.remove("fa-check-square");
            checkbox.classList.add("fa-square");
        }
    }

    function setFilterDate(date) {
        if (selectedSpeeches.length !== 0) {
            selectedSpeeches.forEach((speechId) => {
                const speechCheckbox = document.getElementById(speechId+"-box");
                speechCheckbox.classList.remove("fa-check-square");
                speechCheckbox.classList.add("fa-square");
            })
            selectedSpeeches = [];
        }
        const index = selectedDates.indexOf(date);
        const checkbox = document.getElementById(date+"-box");

        if (index === -1) {
            selectedDates.push(date);
            checkbox.classList.remove("fa-square");
            checkbox.classList.add("fa-check-square");
        } else {
            selectedDates.splice(index, 1);
            checkbox.classList.remove("fa-check-square");
            checkbox.classList.add("fa-square");
        }
    }

    function emptyAllFilters() {
        selectedSpeeches.length !== 0 ? selectedSpeeches.forEach((speechId) => {
                const speechCheckbox = document.getElementById(speechId+"-box");
                speechCheckbox.classList.remove("fa-check-square");
                speechCheckbox.classList.add("fa-square");
            }) : null;
        selectedDates.length !== 0 ? selectedDates.forEach((date) => {
                const dateCheckbox = document.getElementById(date+"-box");
                dateCheckbox.classList.remove("fa-check-square");
                dateCheckbox.classList.add("fa-square");
            }) : null;
        selectedSpeakers !== 0 ? selectedSpeakers.forEach((speakerId) => {
                const speakerCheckbox = document.getElementById(speakerId+"-box");
                speakerCheckbox.classList.remove("fa-check-square");
                speakerCheckbox.classList.add("fa-square");
            }) : null;
        selectedFractions.length !== 0 ? selectedFractions.forEach((fraction) => {
                const fractionCheckbox = document.getElementById(fraction+"-box");
                fractionCheckbox.classList.remove("fa-check-square");
                fractionCheckbox.classList.add("fa-square");
            }) : null;
        selectedSpeeches = [];
        selectedSpeakers = [];
        selectedFractions = [];
        selectedDates = [];
    }

    function fetchChartDataForAllSpeeches() {
        emptyAllFilters();
        const spinnerWrapper = document.getElementById("spinner-wrapper");
        const barChartWrapper = document.getElementById("bar-chart-wrapper");
        const bubbleChartWrapper = document.getElementById("bubble-chart-wrapper");
        const radarChartWrapper = document.getElementById("radar-chart-wrapper");
        const sunburstWrapper = document.getElementById("sunburst-chart-wrapper");
        bubbleChartWrapper.style.display = "none";
        barChartWrapper.style.display = "none";
        radarChartWrapper.style.display = "none";
        sunburstWrapper.style.display = "none";
        spinnerWrapper.innerHTML = "<div class='spinner'><i class='fa-solid fa-spinner fa-spin'></i><p>Lade NLP-Daten für alle Reden...</p></div>";
        $.ajax({
            url: "/nlpcharts/chartdata/fetchchartdataforallspeeches",
            type: "GET",
            contentType: "application/json",
            success: function (response) {
                const posAmount = Object.entries(response.posAmounts).map(([pos, count]) => ({pos, count}));
                const topics = Object.entries(response.topics).map(([topic, value]) => ({ topic, value }));
                const categoryCounts = d3.rollup(response.namedEntities, v => v.length, d => d.category);
                const hierarchyData = {
                    name: "root",
                    children: Array.from(categoryCounts, ([key, value]) => ({ name: key, value: value }))
                };
                spinnerWrapper.innerHTML = "";
                bubbleChartWrapper.style.display = "flex";
                bubbleChartWrapper.style.flexDirection = "column";
                bubbleChartWrapper.style.width = 710;
                barChartWrapper.style.display = "flex";
                barChartWrapper.style.flexDirection = "column";
                barChartWrapper.style.width = 710;
                radarChartWrapper.style.display = "flex";
                radarChartWrapper.style.flexDirection = "column";
                radarChartWrapper.style.width = 710;
                sunburstWrapper.style.display = "flex";
                sunburstWrapper.style.flexDirection = "column";
                sunburstWrapper.style.width = 710;
                createBubbleChart(topics);
                createBarChart(posAmount);
                createRadarChart(response.sentenceSentiments);
                createSunburstChart(hierarchyData);
            },
            error: function (xhr) {
                console.error("Fetch of chart-data failed: " + xhr.responseText);
            }
        });
    }

    function fetchChartData() {
        if(selectedSpeeches.length === 0 && selectedSpeakers.length === 0 && selectedFractions.length === 0 && selectedDates.length === 0) {
            alert("Keine Filter ausgewählt!");
            return
        }
        const spinnerWrapper = document.getElementById("spinner-wrapper");
        const barChartWrapper = document.getElementById("bar-chart-wrapper");
        const bubbleChartWrapper = document.getElementById("bubble-chart-wrapper");
        const radarChartWrapper = document.getElementById("radar-chart-wrapper");
        const sunburstChartWrapper = document.getElementById("sunburst-chart-wrapper")
        bubbleChartWrapper.style.display = "none";
        barChartWrapper.style.display = "none";
        radarChartWrapper.style.display = "none";
        sunburstChartWrapper.style.display = "none";
        spinnerWrapper.innerHTML = "<div class='spinner'><i class='fa-solid fa-spinner fa-spin'></i><p>Lade NLP-Daten...</p></div>";
        $.ajax({
            url: "/nlpcharts/chartdata?speechids=" + selectedSpeeches + "&fractions=" + selectedFractions + "&speakers=" + selectedSpeakers + "&dates=" + selectedDates,
            type: "GET",
            contentType: "application/json",
            success: function (response) {
                const posAmount = Object.entries(response.posAmounts).map(([pos, count]) => ({pos, count}));
                const topics = Object.entries(response.topics).map(([topic, value]) => ({ topic, value }));
                if (posAmount.length === 0 && topics.length === 0) {
                    spinnerWrapper.innerHTML = "<div class='no-speeches-found'><i class='fa-solid fa-triangle-exclamation fa-bounce fa-xl'></i><p>Keine Reden zu ausgewähltem Filter gefunden!</p></div>";
                    return;
                }
                const categoryCounts = d3.rollup(response.namedEntities, v => v.length, d => d.category);
                const hierarchyData = {
                    name: "root",
                    children: Array.from(categoryCounts, ([key, value]) => ({ name: key, value: value }))
                };

                spinnerWrapper.innerHTML = "";
                bubbleChartWrapper.style.display = "flex";
                bubbleChartWrapper.style.flexDirection = "column";
                bubbleChartWrapper.style.width = 710;
                barChartWrapper.style.display = "flex";
                barChartWrapper.style.flexDirection = "column";
                barChartWrapper.style.width = 710;
                radarChartWrapper.style.display = "flex";
                radarChartWrapper.style.flexDirection = "column";
                radarChartWrapper.style.width = 710;
                sunburstChartWrapper.style.display = "flex";
                sunburstChartWrapper.style.flexDirection = "column";
                sunburstChartWrapper.style.width = 710;
                createBubbleChart(topics)
                createBarChart(posAmount);
                createRadarChart(response.sentenceSentiments)
                createSunburstChart(hierarchyData);
            },
            error: function (xhr) {
                console.error("Fetch of chart-data failed: " + xhr.responseText);
            }
        });
    }

    function setSession(sessionId) {
        document.getElementById("speeches-table").innerHTML = "<div class='spinner'><i class='fa-solid fa-spinner fa-spin'></i><p>Lade Reden für Sitzung "+sessionId+"</p></div>";
        selectedSpeeches = [];
        $.ajax({
            url: "/nlpcharts/sessionchange?id=" + sessionId,
            type: "GET",
            contentType: "application/json",
            success: function (response) {
                let tableHTML = `
                    <caption style="padding-bottom: 10px" id="speech-table-caption">Reden für Sitzung `+sessionId+`</caption>
                    <thead>
                        <tr class="table-header-row">
                            <th>Sitzung</th>
                            <th>Redner</th>
                            <th>Rede-ID</th>
                            <th>Auswahl</th>
                        </tr>
                    </thead>
                    <tbody>`;

                response.forEach(speech => {
                    tableHTML += `
                        <tr id="`+speech.speechId+`">
                            <td><a href="/speeches?id=`+speech.sessionId+`">`+speech.sessionId+`</a></td>
                            <td><a href="/speakers/portfolio?id=`+speech.speakerId+`">`+speech.speaker+`</a></td>
                            <td><a href="/speeches/speechdetails?id=`+speech.speechId+`">`+speech.speechId+`</a></td>
                            <td onclick="setFilterSpeech('`+speech.speechId+`')">
                                <i id="`+speech.speechId+`-box" class="fa-regular fa-square"></i>
                            </td>
                        </tr>`;
                });

                tableHTML += `</tbody>`;

                document.getElementById("speeches-table").innerHTML = tableHTML;
            },
            error: function (xhr) {
                console.error("Session update failed: " + xhr.responseText);
            }
        })
    }
</script>
</html>