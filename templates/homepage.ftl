<html lang="DE">
<head>
    <title>
        Multimodal Parliament Explorer
    </title>
    <meta charset="UTF-8">
    <script src="https://kit.fontawesome.com/fc91097d0e.js" crossorigin="anonymous"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
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
        .speech-search-wrapper {
            display: flex;
            flex-direction: row;
            align-items: center;
            justify-content: center;
            width: 100%;
            margin-bottom: 10px;
        }
        .header {
            font-size: 20px;
            padding: 0;
            margin: 0;
        }
        .table-wrapper {
            display: flex;
            justify-content: center;
            align-items: flex-start;
            flex-direction: row;
            width: 100%;
        }
        table {
            width: 79%;
            border-collapse: collapse;
            margin-left: 10%;
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
        th {
            padding: 10px;
        }
        .link-to-usecases {
            margin-right: 50px;
        }
        .buttons-div {
            display: flex;
            flex-direction: column;
            justify-content: flex-start;
            align-items: center;
            margin-top: 1rem;
            margin-left: 1rem;
        }
        .export-button {
            width: 100%;
            border: none;
            border-radius: 5px;
            background-color: green;
            color: white;
            box-shadow: 2px 2px gray;
        }
        .select-all-button {
            width: 100%;
            border: none;
            border-radius: 5px;
            margin-bottom: 1rem;
            background-color: black;
            color: white;
            box-shadow: 2px 2px lightgray;
        }
        button {
            height: 2rem;
            font-weight: bold;
        }
        button:hover {
            cursor: pointer;
        }
        .iframe-overlay {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.5);
            display: none; /* Hidden by default */
            justify-content: center;
            align-items: center;
            z-index: 1000;
        }
        .iframe-content {
            background: white;
            width: 90%;
            max-width: 80%;
            padding: 30px;
            border-radius: 5px;
            position: relative;
        }
        .close-iframe {
            position: absolute;
            top: 0;
            right: 0;
            cursor: pointer;
            font-size: 20px;
            background: white;
            font-weight: bold;
            border: none;
        }
    </style>
</head>
<body>
<div>
    <div class="header-div">
        <h1 class="header">Multimodal Parliament Explorer</h1>
    </div>
    <div class="speech-search-wrapper">
        <a href="nlpcharts" class="link-to-usecases">NLP-Charts</a>
        <a href="speakers" class="link-to-usecases">Redner-Liste</a>
        <a href="speeches/searchspeeches">Volltext-Rede-Suche</a>
    </div>
    <div class="table-wrapper">
        <table id="speakerTable">
            <caption style="padding-bottom: 10px">Alle Sitzungen der 20. Wahlperiode</caption>
            <tbody>
            <tr class="table-header-row">
                <th>Sitzung</th>
                <th>Datum</th>
                <th>Anzahl Tagesordnungspunkte</th>
                <th>Auswahl</th>
            </tr>
            <#list sessions as session>
                <tr id="${session.id}" class="table-rows">
                    <td><a href="/speeches?id=${session.id}">${session.id}</a></td>
                    <td>${session.dateString}</td>
                    <td>${session.agendaSize}</td>
                    <td onclick="setProtocolExport('${session.id}')">
                        <i id="${session.id}-box" class="fa-regular fa-square"></i>
                    </td>
                </tr>
            </#list>
            </tbody>
        </table>
        <div class="buttons-div">
            <h4>Protokoll-Export</h4>
            <button class="select-all-button" onclick="selectAllProtocols()">Alle auswählen</button>
            <button class="export-button" id="export-button" onclick="exportPdf()">Export</button>
        </div>
    </div>
</div>
<div id="iframeOverlay" class="iframe-overlay">
    <div class="iframe-content">
        <button class="close-iframe" onclick="closeIframe()">×</button>
        <iframe id="pdfFrame" src="" width="100%" height="600px" style="border: none;"></iframe>
    </div>
</div>
</body>
<script>
    let selectedProtocols = []

    function setProtocolExport(session) {
        const index = selectedProtocols.indexOf(session);
        const checkbox = document.getElementById(session+"-box");

        if (index === -1) {
            selectedProtocols.push(session);
            checkbox.classList.remove("fa-square");
            checkbox.classList.add("fa-check-square");
        } else {
            selectedProtocols.splice(index, 1);
            checkbox.classList.remove("fa-check-square");
            checkbox.classList.add("fa-square");
        }
    }

    function selectAllProtocols() {
        const tr = document.getElementsByClassName("table-rows");
        for (i=0; i < tr.length; i++) {
            setProtocolExport(tr[i].id);
        }
    }

    function exportPdf() {
        if (selectedProtocols.length === 0) {
            alert("Keine Sitzungen ausgewählt!")
            return
        }
        document.getElementById("pdfFrame").src = "/export/sessions?ids=" + selectedProtocols;
        document.getElementById("iframeOverlay").style.display = "flex";
    }
    function closeIframe() {
        document.getElementById("iframeOverlay").style.display = "none";
        document.getElementById("pdfFrame").src = "";
    }
</script>
</html>