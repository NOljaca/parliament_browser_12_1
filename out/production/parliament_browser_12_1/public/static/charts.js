function createBarChart(data) {
    const totalWidth = 700;
    const margin = { top: 20, right: 20, bottom: 50, left: 20 };
    const width = totalWidth - margin.left - margin.right; // 660
    const height = 400; // Chart height

    d3.select("#bar-chart").selectAll("*").remove();

    const svg = d3.select("#bar-chart")
        .attr("width", totalWidth)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform", `translate(${margin.left}, ${margin.top})`);

    const colorScale = d3.scaleOrdinal()
        .domain(data.map(d => d.pos))
        .range(d3.quantize(d3.interpolateRainbow, data.length));

    const x = d3.scaleBand()
        .domain(data.map(d => d.pos))
        .range([0, width])
        .padding(0.1);

    const y = d3.scaleLinear()
        .domain([0, d3.max(data, d => d.count)])
        .range([height, 0]);

    svg.append("g")
        .attr("transform", `translate(0, ${height})`)
        .call(d3.axisBottom(x))
        .selectAll("text")
        .attr("transform", "rotate(-45)")
        .style("text-anchor", "end");

    svg.append("g")
        .call(d3.axisLeft(y));

    svg.selectAll(".bar")
        .data(data)
        .enter()
        .append("rect")
        .attr("class", "bar")
        .attr("x", d => x(d.pos))
        .attr("y", d => y(d.count))
        .attr("width", x.bandwidth())
        .attr("height", d => height - y(d.count))
        .attr("fill", d => colorScale(d.pos));

    svg.selectAll(".label")
        .data(data)
        .enter()
        .append("text")
        .attr("x", d => x(d.pos) + x.bandwidth() / 2)
        .attr("y", d => y(d.count) - 5)
        .style("text-anchor", "middle")
        .text(d => d.count);
}

function createBubbleChart(data) {
    const width = 500;
    const height = 500;
    const padding = 10;

    const root = d3.hierarchy({ children: data })
        .sum(d => d.value)
        .sort((a, b) => b.value - a.value);

    const packLayout = d3.pack()
        .size([width, height])
        .padding(padding);

    packLayout(root);

    const colorScale = d3.scaleOrdinal()
        .domain(root.leaves().map(d => d.data.topic))
        .range(d3.quantize(d3.interpolateRainbow, root.leaves().length));

    const svg = d3.select("#bubble-chart")
        .attr("width", width)
        .attr("height", height);
    svg.selectAll("*").remove();

    const node = svg.selectAll("g")
        .data(root.leaves())
        .enter()
        .append("g")
        .attr("transform", d => `translate(${d.x},${d.y})`);

    node.append("circle")
        .attr("r", d => d.r)
        .attr("fill", d => colorScale(d.data.topic));

    node.append("text")
        .attr("dy", "0.3em")
        .attr("text-anchor", "middle")
        .text(d => d.data.topic)
        .style("font-size", d => Math.min(2 * d.r, 16) + "px")
        .style("fill", "black");
}

function createRadarChart(sentiments) {
    const buckets = {
        "-1": { count: 0, speeches: [] },
        "-0.5": { count: 0, speeches: [] },
        "0": { count: 0, speeches: [] },
        "0.5": { count: 0, speeches: [] },
        "1": { count: 0, speeches: [] }
    };

    for (const speechId in sentiments) {
        const s = sentiments[speechId];
        let bucket;
        if (s < -0.75) {
            bucket = "-1";
        } else if (s < -0.25) {
            bucket = "-0.5";
        } else if (s < 0.25) {
            bucket = "0";
        } else if (s < 0.75) {
            bucket = "0.5";
        } else {
            bucket = "1";
        }
        buckets[bucket].count += 1;
        buckets[bucket].speeches.push(speechId);
    }

    const data = Object.keys(buckets).map(key => ({
        axis: key,
        value: buckets[key].count,
        speeches: buckets[key].speeches
    }));

    d3.select("#radar-chart").selectAll("*").remove();

    const totalWidth = 500, totalHeight = 500;
    const margin = { top: 50, right: 50, bottom: 50, left: 50 };
    const width = totalWidth - margin.left - margin.right;
    const height = totalHeight - margin.top - margin.bottom;
    const radius = Math.min(width, height) / 2;

    const svg = d3.select("#radar-chart")
        .attr("width", totalWidth)
        .attr("height", totalHeight)
        .append("g")
        .attr("transform", `translate(${totalWidth / 2}, ${totalHeight / 2})`);

    const maxValue = d3.max(data, d => d.value);
    const rScale = d3.scaleLinear()
        .domain([0, maxValue])
        .range([0, radius]);

    const totalAxes = data.length; // 5 axes (buckets)
    const angleSlice = (2 * Math.PI) / totalAxes;

    const gridLevels = 5;
    for (let i = 1; i <= gridLevels; i++) {
        svg.append("circle")
            .attr("r", rScale(maxValue * i / gridLevels))
            .attr("fill", "none")
            .attr("stroke", "lightgray")
            .attr("stroke-dasharray", "2,2");
    }

    const axisGroup = svg.append("g").attr("class", "axisWrapper");
    data.forEach((d, i) => {
        const angle = i * angleSlice - Math.PI / 2;
        const x = rScale(maxValue) * Math.cos(angle);
        const y = rScale(maxValue) * Math.sin(angle);
        axisGroup.append("line")
            .attr("x1", 0)
            .attr("y1", 0)
            .attr("x2", x)
            .attr("y2", y)
            .attr("stroke", "gray")
            .attr("stroke-width", 1);
        const labelOffset = 10;
        const labelX = (rScale(maxValue) + labelOffset) * Math.cos(angle);
        const labelY = (rScale(maxValue) + labelOffset) * Math.sin(angle);
        axisGroup.append("text")
            .attr("x", labelX)
            .attr("y", labelY)
            .attr("dy", "0.35em")
            .style("font-size", "10px")
            .style("text-anchor", () => {
                if (Math.cos(angle) < -0.1) return "end";
                else if (Math.cos(angle) > 0.1) return "start";
                else return "middle";
            })
            .text(d.axis);
    });

    const radarLine = d3.lineRadial()
        .radius(d => rScale(d.value))
        .angle((d, i) => i * angleSlice - Math.PI / 2)
        .curve(d3.curveLinearClosed);

    svg.append("path")
        .datum(data)
        .attr("d", radarLine)
        .attr("fill", "steelblue")
        .attr("fill-opacity", 0.5)
        .attr("stroke", "blue")
        .attr("stroke-width", 2);

    const circles = svg.selectAll(".radarCircle")
        .data(data)
        .enter()
        .append("g")
        .attr("class", "radarCircle")
        .attr("transform", (d, i) => {
            const angle = i * angleSlice - Math.PI / 2;
            return `translate(${rScale(d.value) * Math.cos(angle)}, ${rScale(d.value) * Math.sin(angle)})`;
        });

    circles.append("circle")
        .attr("r", 4)
        .style("fill", "blue")
        .style("fill-opacity", 0.8);

    circles.append("title")
        .text(d => `Speech IDs: ${d.speeches.join(", ")}`);
}

function createSunburstChart(data) {
    const width = 500;
    const height = 500;
    const radius = Math.min(width, height) / 2;

    d3.select("#sunburst-chart").selectAll("*").remove();

    const svg = d3.select("#sunburst-chart")
        .attr("width", width)
        .attr("height", height)
        .append("g")
        .attr("transform", `translate(${width / 2}, ${height / 2})`);

    const partition = d3.partition()
        .size([2 * Math.PI, radius]);

    const root = d3.hierarchy(data)
        .sum(d => d.value);

    partition(root);

    const arc = d3.arc()
        .startAngle(d => d.x0)
        .endAngle(d => d.x1)
        .innerRadius(d => d.y0)
        .outerRadius(d => d.y1);

    const color = d3.scaleOrdinal()
        .domain(root.children.map(d => d.data.name))
        .range(d3.schemeCategory10);

    svg.selectAll("path")
        .data(root.descendants().filter(d => d.depth > 0))
        .enter().append("path")
        .attr("d", arc)
        .attr("fill", d => color(d.data.name))
        .attr("stroke", "#fff")
        .attr("stroke-width", 1)
        .append("title")
        .text(d => `${d.data.name}: ${d.value}`);

    svg.selectAll("text")
        .data(root.descendants().filter(d => d.depth > 0))
        .enter()
        .append("text")
        .attr("transform", function(d) {
            const angle = ((d.x0 + d.x1) / 2) * (180 / Math.PI) - 90;
            const r = (d.y0 + d.y1) / 2;
            return `rotate(${angle}) translate(${r}, 0) ${angle > 90 ? "rotate(180)" : ""}`;
        })
        .attr("dy", "0.35em")
        .style("font-size", "10px")
        .style("text-anchor", function(d) {
            const angle = ((d.x0 + d.x1) / 2) * (180 / Math.PI) - 90;
            return angle > 90 ? "end" : "start";
        })
        .text(d => d.data.name);
}

