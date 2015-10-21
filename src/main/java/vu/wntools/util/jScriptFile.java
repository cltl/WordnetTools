package vu.wntools.util;

/**
 * Created by piek on 13/06/15.
 */
public class jScriptFile {

    static String part1 = "<!DOCTYPE html>\n" +
            "<meta charset=\"utf-8\">\n" +
            "<style>\n" +
            "\n" +
            ".node circle {\n" +
            "  <!-- fill: #fff; -->\n" +
            "  <!--stroke: steelblue; -->\n" +
            "  stroke-width: 4px;\n" +
            "}\n" +
            "\n" +
            ".node {\n" +
            "  font: 10px sans-serif;\n" +
            "}\n" +
            "\n" +
            ".link {\n" +
            "  fill: none;\n" +
            "  stroke: #ccc;\n" +
            "  stroke-width: 1.5px;\n" +
            "}\n" +
            "\n" +
            "</style>\n" +
            "<body>\n" +
            "<!-- <script src=\"http://d3js.org/d3.v3.min.js\"></script> -->\n" +
            "<script src= \"../d3.v3.min.js\"></script>\n" +
            "<!-- <script src=\"http://www.d3plus.org/js/d3plus.js\"></script> -->\n" +
            "\n" +
            "<script>\n" +
            "\n" +
            "<!-- var diameter = 960; -->\n" +
            "var diameter = ";
    static String part2 = "; \n" +
            "var tree = d3.layout.tree()\n" +
            "    .size([360, diameter / 2 - 120])\n" +
            "    .separation(function(a, b) { return (a.parent == b.parent ? 1 : 2) / a.depth; });\n" +
            "\n" +
            "var diagonal = d3.svg.diagonal.radial()\n" +
            "    .projection(function(d) { return [d.y, d.x / 180 * Math.PI]; });\n" +
            "\n" +
            "var svg = d3.select(\"body\").append(\"svg\")\n" +
            "    .attr(\"width\", diameter)\n" +
            "    .attr(\"height\", diameter - 300)\n" +
            "  .append(\"g\")\n" +
            "    .attr(\"transform\", \"translate(\" + diameter / 2 + \",\" + diameter / 2 + \")\");\n" +
            "\n" +
            "d3.json(\"";
    static String part3 = "\", function(error, root) {\n" +
            "  var nodes = tree.nodes(root),\n" +
            "      links = tree.links(nodes);\n" +
            "\n" +
            "  var link = svg.selectAll(\".link\")\n" +
            "      .data(links)\n" +
            "    .enter().append(\"path\")\n" +
            "      .attr(\"class\", \"link\")\n" +
            "            .style(\"stroke\", function(d) { return d.target.color; })\n" +
            "\n" +
            "      .attr(\"d\", diagonal);\n" +
            "\n" +
            "  var node = svg.selectAll(\".node\")\n" +
            "      .data(nodes)\n" +
            "    .enter().append(\"g\")\n" +
            "      .attr(\"class\", \"node\")\n" +
            "      .style(\"fill\", function(d){return d.color;})\n" +
            "      .attr(\"transform\", function(d) { return \"rotate(\" + (d.x - 90) + \")translate(\" + d.y + \")\"; })\n" +
            "\n" +
            "  node.append(\"circle\")\n" +
            "      .style(\"fill\", function(d){return d.color;})\n" +
            "      .attr(\"r\", 4.5);\n" +
            "\n" +
            "  node.append(\"text\")\n" +
            "      .attr(\"dy\", \".31em\")\n" +
            "      .attr(\"fill\",\"black\") <!-- makes the text black -->\n" +
            "      .attr(\"text-anchor\", function(d) { return d.x < 180 ? \"start\" : \"end\"; })\n" +
            "      .attr(\"transform\", function(d) { return d.x < 180 ? \"translate(8)\" : \"rotate(180)translate(-8)\"; })\n" +
            "      \n" +
            "      .call(wrap, \"10\")\n" +
            "      .text(function(d) { return d.name; });\n" +
            "      \n" +
            "    var insertLinebreaks = function (t, d, width) {\n" +
            "    var el = d3.select(t);\n" +
            "    var p = d3.select(t.parentNode);\n" +
            "    p.append(\"foreignObject\")\n" +
            "        .attr('x', -width/2)\n" +
            "        .attr(\"width\", width)\n" +
            "        .attr(\"height\", 200)\n" +
            "      .append(\"xhtml:p\")\n" +
            "        .attr('style','word-wrap: break-word; text-align:center;')\n" +
            "        .html(d);    \n" +
            "\n" +
            "    el.remove();\n" +
            "\n" +
            "};\n" +
            "\n" +
            "function wrap(text, width) {\n" +
            "  text.each(function() {\n" +
            "    var text = d3.select(this),\n" +
            "        words = text.text().split(/;+/).reverse(),\n" +
            "        word,\n" +
            "        line = [],\n" +
            "        lineNumber = 0,\n" +
            "        lineHeight = 1.1, // ems\n" +
            "        y = text.attr(\"y\"),\n" +
            "        dy = parseFloat(text.attr(\"dy\")),\n" +
            "        tspan = text.text(null).append(\"tspan\").attr(\"x\", 0).attr(\"y\", y).attr(\"dy\", dy + \"em\");\n" +
            "    while (word = words.pop()) {\n" +
            "      line.push(word);\n" +
            "      tspan.text(line.join(\" \"));\n" +
            "      if (tspan.node().getComputedTextLength() > width) {\n" +
            "        line.pop();\n" +
            "        tspan.text(line.join(\" \"));\n" +
            "        line = [word];\n" +
            "        tspan = text.append(\"tspan\").attr(\"x\", 0).attr(\"y\", y).attr(\"dy\", ++lineNumber * lineHeight + dy + \"em\").text(word);\n" +
            "      }\n" +
            "    }\n" +
            "  });\n" +
            "} \n" +
            " \n" +
            "});\n" +
            "\n" +
            "\n" +
            "\n" +
            "d3.select(self.frameElement).style(\"height\", diameter - 150 + \"px\");\n" +
            "\n" +
            "</script>";

    static public String makeFile (String fileName, int nConcepts) {
        String size = "2000";
       /* if (nConcepts<500) {
            size = "500";
        }
        else if (nConcepts<2000) {
            size = "1000";
        }
*//*
        else if (nConcepts<4000) {
            size = "6500";
        }
*//*
        else if (nConcepts<6000) {
            size = "2000";
        }
*//*
        else if (nConcepts<8000) {
            size = "10000";
        }
*//*
        else {
            size = "4000";
        }*/
        String str = part1+size+part2+fileName+part3;
        return str;
    }
}
