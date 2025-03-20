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
