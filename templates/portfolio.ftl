<!DOCTYPE html>
<html lang="de">
<head>
    <meta charset="UTF-8">
    <title>${speaker.name} ${speaker.surname}</title>
    <style>
        .speech-index {
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            border-bottom: 2px solid black;
            padding-bottom: 20px;
            padding-top: 5px;
        }
        a {
            text-decoration: none;
            font-weight: bold;
            color: black;
        }
        .speech-index-header {
            display: flex;
            flex-direction: row;
            justify-content: space-between;
            align-items: center;
            width: 100%;
            height: 10%;
            background-color: gray;
            color: white;
        }
        .image-container {
            display: flex;
            flex-direction: column;
            width: 15%;
            justify-content: center;
            align-items: center;
        }
        .image-wrapper {
            display: flex;
            flex-direction: row;
            align-items: center;
        }
        .export-button {
            margin-bottom: 10px;
            background-color: green;
            color: white;
            border: none;
            border-radius: 5px;
            box-shadow: 1px 1px gray;
            font-size: 1rem;
            font-weight: bold;
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
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
</head>
<body>
<h1>${speaker.name} ${speaker.surname} (${speaker.fraction.shortName!""})</h1>
<p>Alter: <strong>${speaker.age!""}</strong></p>
<p>Geburtsdatum: <strong>${speaker.birthDateString!""}</strong></p>
<p>Geschlecht: <strong>${speaker.gender!""}</strong></p>
<p>Beruf: <strong>${speaker.profession!""}</strong></p>
<button class="export-button" onclick="exportPdf(${speaker.id})">
    Reden inkl. Analyse exportieren
</button>
<div class="image-wrapper">
    <div class="image-container">
        <img src="" alt="" style="width: 200px; height: 200px;">
    </div>
</div>
<div class="speech-index">
    <div class="speech-index-header">
        <h3 style="margin-left: 38%">Alle Reden von ${speaker.name} ${speaker.surname}</h3>
        <p style="margin-right: 2%; font-size: 10px">(Sortiert nach: Datum aufsteigend)</p>
    </div>
    <div class="speech-index-content">
        <#if speeches?has_content>
            <#list speeches as speech>
                <p>${speech.session.dateString}: <a href="/speeches/speechdetails?id=${speech.id}">${speech.id}</a></p>
            </#list>
        </#if>
    </div>
</div>

<div id="iframeOverlay" class="iframe-overlay">
    <div class="iframe-content">
        <button class="close-iframe" onclick="closeIframe()">×</button>
        <iframe id="pdfFrame" src="" width="100%" height="600px" style="border: none;"></iframe>
    </div>
</div>

<script>
    function exportPdf(speakerId) {
        document.getElementById("pdfFrame").src = "/export/speaker?id=" + speakerId;
        document.getElementById("iframeOverlay").style.display = "flex";
    }
    function closeIframe() {
        document.getElementById("iframeOverlay").style.display = "none";
        document.getElementById("pdfFrame").src = "";
    }
</script>
</body>
</html>
