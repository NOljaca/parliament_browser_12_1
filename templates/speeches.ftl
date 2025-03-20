<html>
<head>
    <title>Multimodal Parliament Browser</title>
    <meta charset="UTF-8">
    <style>
        .title-container {
            display: flex;
            align-items: center;
            justify-content: center;
            margin-bottom: 10px;
        }
        .session-details-container {
            display: flex;
            flex-direction: column;
            align-items: flex-start;
            justify-content: flex-start;
            margin-bottom: 0;
        }
        h3 {margin: 0;}
        .session-meta-data-container {
            display: flex;
            flex-direction: column;
            margin: 0;
        }
        .session-meta-data {
            text-decoration: none;
            list-style-type: none;
            padding: 10px 0;
            margin: 0;
        }
        .speech-list-container {
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
        .agenda-list {
            padding-top: 10px;
        }
        .agenda {
            padding: 10px 0;
        }
        .agenda-id {
            font-weight: bold;
        }
    </style>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
</head>
<body>
<div class="title-container">
   <h1 class="title">Alle Reden der ${session.title}</h1>
</div>
<div class="session-details-container">
    <h3>Details zur ${session.title}</h3>
    <div class="session-meta-data-container">
        <ul class="session-meta-data">
            <li>Datum: ${session.dateString}</li>
            <li>Anzahl der Tagesordnungen: ${session.agendaSize}
                <ul class="agenda-list">
                    <#list agendas as agenda>
                        <li class="agenda"><span class="agenda-id">${agenda.id}</span> - ${agenda.title}</li>
                    </#list>
                </ul>
            </li>
        </ul>
    </div>
</div>
<div class="speech-list-container">
    <table id="speakerTable">
        <caption style="padding-bottom: 10px">Alle Reden der ${session.title}</caption>
        <tbody>
        <tr class="table-header-row">
            <th>Tagesordnungspunkt</th>
            <th>Redner</th>
            <th>Rede-ID</th>
        </tr>
        <#list speeches as speech>
            <tr id="${speech.speechId}">
                <td>${speech.agendaId}</td>
                <td>${speech.speaker}</td>
                <td><a href="/speeches/speechdetails?id=${speech.speechId}">${speech.speechId}</a></td>
            </tr>
        </#list>
        </tbody>
    </table>
</div>
</body>
<script>
</script>
</html>