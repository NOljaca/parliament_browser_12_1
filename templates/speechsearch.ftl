<html>
<head>
    <title>
        Volltext-Redensuche
    </title>
    <meta charset="UTF-8">
    <script src="https://kit.fontawesome.com/fc91097d0e.js" crossorigin="anonymous"></script>
    <style>
        .page-wrapper {
            display: flex;
            flex-direction: column;
        }
        .header-wrapper {
            display: flex;
            width: 100%;
            height: 10%;
            justify-content: center;
            align-items: center;
        }

        .search-field-wrapper {
            display: flex;
            width: 100%;
            height: 20%;
            justify-content: center;
            align-items: center;
            flex-direction: column;
        }

        .input-wrapper {
            display: flex;
            flex-direction: row;
            width: 100%;
            justify-content: center;
            margin-bottom: 30px;
        }

        input {
            width: 25%;
            border: none;
            border-bottom: 1px solid lightgray;
            outline: none;
        }

        button {
            margin-left: 15px;
            border: none;
            color: white;
            background-color: green;
            border-radius: 30px;
            cursor: pointer;
            width: 100px;
            height: 30px;
            box-shadow: 1px 1px lightgray;
            font-weight: bold;
        }

        .search-result-wrapper {
            display: flex;
            justify-content: center;
            align-items: center;
        }

        #search-result {
            width: 100%;
        }

        td {
            text-align: center;
            padding-top: 10px;
        }

        a {
            color: gray;
        }
        .spinner-wrapper {
            margin-top: 10px;
            font-size: 1rem;
            display: none;
            justify-content: center;
            align-items: center;
            flex-direction: column;
        }
    </style>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
</head>
<body>
<div class="page-wrapper">
    <div class="header-wrapper">
        <h1>Volltext-Redensuche</h1>
    </div>
    <div class="search-field-wrapper">
        <h3>Suchen Sie nach einer Rede</h3>
        <div class="input-wrapper">
            <input type="text" id="search-input" placeholder="Geben Sie einen Text ein..."/>
            <button onclick="fetchSpeeches()">Suchen</button>
        </div>
    </div>
    <div class="search-result-wrapper">
        <table id="search-result">
            <caption id="result-amount"></caption>
            <tbody id="search-result-body">

            </tbody>
        </table>
    </div>
    <div class="spinner-wrapper" id="spinner-wrapper">
        <i class='fa-solid fa-spinner fa-spin'></i>
        <p>Lade Reden...</p>
    </div>
</div>
</body>
<script>
    function fetchSpeeches() {
        const searchInput = document.getElementById("search-input").value;
        const spinnerWrapper = document.getElementById("spinner-wrapper");
        const table = document.getElementById("search-result");
        table.style.display = "none";
        spinnerWrapper.style.display = "flex";
        $.ajax({
            url: "/speeches/searchspeeches/fetchspeeches?text=" + searchInput,
            method: "GET",
            success: function (data) {
                spinnerWrapper.style.display = "none";
                table.style.display = "";
                if (data.amount > 100) {
                    document.getElementById("result-amount").innerHTML = "Gefundene Reden: " + data.amount + ", es werden 100 angezeigt";
                } else {
                    document.getElementById("result-amount").innerHTML = "Gefundene Reden: " + data.amount;
                }
                let tableBody = document.getElementById("search-result-body");
                tableBody.innerHTML = `<tr>
                                        <th>Rede-ID</th>
                                   </tr>`;
                data.speeches.forEach(speech => {
                    tableBody.innerHTML += `
                       <tr>
                            <td><a href="speechdetails?id=`+speech.speechId+`">`+speech.speechId+`</a></td>
                        </tr>
                    `;
                });
            },
            error: function () {
                alert("Fehler bei Reden-Suche");
            }
        });
    }
</script>
</html>