<html lang="DE">
<head>
    <title>
        Rednerliste
    </title>
    <meta charset="UTF-8">
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
            flex-direction: column;
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
            align-items: center;
            flex-direction: column;
        }
        table {
            width: 80%;
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
        th {
            padding: 10px;
        }

        #searchBar {
            width: 40%;
        }
    </style>
</head>
<body>
<div>
    <div class="header-div">
        <h1 class="header">Rednerliste der 20. Wahlperiode</h1>
    </div>
    <div class="table-wrapper">
        <input type="text" id="searchBar" onkeyup="searchSpeaker()" placeholder="Suche nach einem Redner/einer Rednerin...">
        <table id="speakerTable">
            <caption style="padding-bottom: 10px">Alle Redner im Bundestag</caption>
            <tbody>
            <tr class="table-header-row">
                <th>ID</th>
                <th>Vorname</th>
                <th>Nachname</th>
                <th>Fraktion</th>
                <th>Anzahl Reden</th>
            </tr>
            <#list speakers as speaker>
                <tr id="${speaker.id}">
                    <td><a href="speakers/portfolio?id=${speaker.id}">${speaker.id}</a></td>
                    <td><a href="speakers/portfolio?id=${speaker.id}">${speaker.name}</a></td>
                    <td><a href="speakers/portfolio?id=${speaker.id}">${speaker.surname}</a></td>
                    <td>${speaker.fraction.shortName}</td>
                    <td>${speaker.speechAmount!"0"}</td>
                </tr>
            </#list>
            </tbody>
        </table>
    </div>
</div>
</body>
<script>
    function searchSpeaker() {
        let input = document.getElementById("searchBar");
        let filter = input.value.toUpperCase();
        let table = document.getElementById("speakerTable");
        let tr = table.getElementsByTagName("tr");

        for (let i = 1; i < tr.length; i++) {
            let nameTd = tr[i].getElementsByTagName("td")[1];
            let surnameTd = tr[i].getElementsByTagName("td")[2];

            if (nameTd && surnameTd) {
                let name = nameTd.textContent || nameTd.innerText;
                let surname = surnameTd.textContent || surnameTd.innerText;
                let nameSurname = (name + " " + surname).toUpperCase();

                if (nameSurname.indexOf(filter) > -1) {
                    tr[i].style.display = "";
                } else {
                    tr[i].style.display = "none";
                }
            }
        }
    }
</script>
</html>