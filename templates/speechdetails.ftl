<html>
<head>
    <title>${speech.id}</title>
    <meta charset="UTF-8">
    <script src="https://kit.fontawesome.com/fc91097d0e.js" crossorigin="anonymous"></script>
    <style>
        .comment {
            color: red;
            margin: 0 5px;
        }

        .speech {
            border: 1px solid black;
            margin: 10px;
        }

        a {
            text-decoration: none;
            font-weight: bold;
            color: black;
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
        .legend {
            border: 1px solid black;
        }
        .legend-location {
            width: 10px;
            height: 10px;
            color: #add8e6;
        }
        .legend-person {
            width: 10px;
            height: 10px;
            color: #ff9696;
        }
        .legend-organization {
            width: 10px;
            height: 10px;
            color: #90ee90;
        }
        .legend-misc {
            width: 10px;
            height: 10px;
            color: #fdfd57;
        }
        button {
            margin: 5px;
            border: none;
            font-size: 1rem;
            border-radius: 5px;
            background-color: green;
            font-weight: bold;
            color: white;
            cursor: pointer;
        }
    </style>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
</head>
<body>
<div class="speaker-details">
    <h3>Details zum Redner</h3>
    <h4><a href="/speakers/portfolio?id=${speaker.id}">${speaker.name} ${speaker.surname} (${speaker.fraction.shortName!""})</a></h4>
    <p>Alter: <strong>${speaker.age!""}</strong></p>
    <p>Geburtsdatum: <strong>${speaker.birthDateString!""}</strong></p>
    <p>Geschlecht: <strong>${speaker.gender!""}</strong></p>
    <p>Beruf: <strong>${speaker.profession!""}</strong></p>
    <div class="image-wrapper">
        <div class="image-container">
            <img src="" style="width: 200px; height: 200px;">
        </div>
    </div>
</div>
<div class="speech-details">
    <div class="speech-meta-data">
        <h3>Details zur Rede ${speech.id}</h3>
        <ul>
            <li>Datum: ${speech.session.dateString}</li>
            <li>Sitzung: ${speech.session.id}</li>
            <li>Tagesordnungspunkt: ${speech.agenda.id} - ${speech.agenda.title}</li>
        </ul>
        <div class="speech">
            <button id="toggle-named-entities">Named-Entity-Anzeige an/aus</button>
            <div id="legend" class="legend">
                <h4>Legende</h4>
                <ul>
                    <li>Person: <span class="legend-person"><i class="fa-solid fa-square"></i></span></li>
                    <li>Organisation: <span class="legend-organization"><i class="fa-solid fa-square"></i></span></li>
                    <li>Ort: <span class="legend-location"><i class="fa-solid fa-square"></i></span></li>
                    <li>MISC: <span class="legend-misc"><i class="fa-solid fa-square"></i></span></li>
                </ul>
            </div>
            <p id="speech-content">${speech.getContentWithComments()}</p>
        </div>
    </div>
</div>
</body>
<script>
    document.addEventListener("DOMContentLoaded", function () {
        const sentences = ${sentences};
        const contentElement = document.getElementById("speech-content");
        let content = contentElement.innerHTML;

        sentences.forEach(sentenceMap => {
            const sentence = sentenceMap.sentence;
            const sentiment = sentenceMap.sentiment;
            const infoIcon = `<span title="Sentiment: `+sentiment+`" style="cursor:pointer; color:gray; font-size: 10px"><i class="fa-solid fa-circle-info"></i></span>`;

            const index = content.indexOf(sentence);
            if (index !== -1) {
                const beforeIcon = content.substring(0, index + sentence.length);
                const afterIcon = content.substring(index + sentence.length);
                content = beforeIcon + infoIcon + afterIcon;
            }
        });
        contentElement.innerHTML = content;

        const namedEntities = ${namedEntities};

        const categoryColors = {
            LOC: '#add8e6',
            ORG: '#90ee90',
            MISC: '#fdfd57',
            PER: '#ff9696'
        };

        // Iterate over each named entity and wrap the matching text with a span.
        // Note: This simple approach replaces ALL occurrences of the word.
        namedEntities.forEach(entity => {
            const text = entity.namedEntity;
            const category = entity.category;
            const color = categoryColors[category] || 'lightgray';
            // Build a span that has a class for toggling and a data attribute for the category.
            const replacement = `<span class="named-entity" data-category="`+category+`" style="background-color: `+color+`;">`+text+`</span>`;
            // Use a global regex to replace all occurrences. (Be cautious with special regex characters in text!)
            const escapedText = text.replace(/[.*+?^\$\{\}()|[\]\\]/g, '\\$&');
            const re = new RegExp(escapedText, 'g');
            content = content.replace(re, replacement);
        });
        contentElement.innerHTML = content;

        // Optional: Setup toggle functionality.
        // For example, a button with id="toggle-named-entities" could toggle the highlighting:
        const toggleButton = document.getElementById("toggle-named-entities");
        if(toggleButton) {
            toggleButton.addEventListener("click", function(){
                const spans = document.querySelectorAll("span.named-entity");
                const legend = document.getElementById("legend");
                spans.forEach(span => {
                    // If backgroundColor is set, remove it; otherwise, reapply based on the data-category.
                    if(span.style.backgroundColor) {
                        span.style.backgroundColor = "";
                        legend.style.display = "none";
                    } else {
                        const cat = span.getAttribute("data-category");
                        span.style.backgroundColor = categoryColors[cat] || "lightgray";
                        legend.style.display = "";
                    }
                });
            });
        }
    })
</script>
</html>