const fs = require('fs');
const path = require('path');

async function main() {
  const { Document, Packer, Paragraph, TextRun, Table, TableRow, TableCell,
          Header, Footer, AlignmentType, LevelFormat,
          HeadingLevel, BorderStyle, WidthType, ShadingType,
          PageNumber, PageBreak, TableOfContents } = await import('docx');

  const CW = 9360;
  const BR = { style: BorderStyle.SINGLE, size: 1, color: 'CCCCCC' };
  const BRS = { top: BR, bottom: BR, left: BR, right: BR };

  const H1 = t => new Paragraph({ heading: HeadingLevel.HEADING_1, spacing: { before: 400, after: 200 }, children: [new TextRun(t)] });
  const H2 = t => new Paragraph({ heading: HeadingLevel.HEADING_2, spacing: { before: 300, after: 150 }, children: [new TextRun(t)] });
  const H3 = t => new Paragraph({ heading: HeadingLevel.HEADING_3, spacing: { before: 240, after: 120 }, children: [new TextRun(t)] });
  const P = (t) => new Paragraph({ spacing: { after: 120, line: 276 }, children: [new TextRun({ text: t, size: 22 })] });
  const Code = lines => lines.map(l => new Paragraph({
    shading: { fill: 'F5F5F5', type: ShadingType.CLEAR },
    spacing: { line: 220 }, indent: { left: 360 },
    children: [new TextRun({ text: l || ' ', font: 'Consolas', size: 18 })]
  }));
  
  function makeTable(headers, rows) {
    const cw = Math.floor(CW / headers.length);
    return new Table({
      width: { size: CW, type: WidthType.DXA },
      columnWidths: headers.map(() => cw),
      rows: [
        new TableRow({ children: headers.map(h => new TableCell({
          borders: BRS, width: { size: cw, type: WidthType.DXA },
          shading: { fill: 'E8F4FD', type: ShadingType.CLEAR },
          children: [new Paragraph({ alignment: AlignmentType.CENTER, children: [new TextRun({ text: h, bold: true, size: 20 })] })]
        })) }),
        ...rows.map(row => new TableRow({
          children: row.map(cell => new TableCell({
            borders: BRS, width: { size: cw, type: WidthType.DXA },
            children: [new Paragraph({ spacing: { line: 260 }, children: [new TextRun({ text: cell, size: 20 })] })]
          }))
        }))
      ]
    });
  }

  // Read the markdown source
  const mdPath = path.join(__dirname, 'warm-flow-npm-refactoring-plan.md');
  const mdContent = fs.readFileSync(mdPath, 'utf-8');
  const mdLines = mdContent.split('\n');

  // Convert markdown to docx paragraphs
  const children = [];
  let inCodeBlock = false;
  let codeLines = [];
  let inTable = false;
  let tableRows = [];
  let tableHeaders = [];

  for (let i = 0; i < mdLines.length; i++) {
    const line = mdLines[i];
    
    // Code blocks
    if (line.trim().startsWith('```')) {
      if (inCodeBlock) {
        children.push(...Code(codeLines));
        codeLines = [];
      }
      inCodeBlock = !inCodeBlock;
      continue;
    }
    if (inCodeBlock) {
      codeLines.push(line);
      continue;
    }

    // Skip empty lines in certain contexts
    if (line.trim() === '') {
      // children.push(new Paragraph({ children: [] }));
      continue;
    }

    // Headers
    if (line.startsWith('# ')) {
      children.push(H1(line.substring(2).trim()));
      continue;
    }
    if (line.startsWith('## ')) {
      children.push(H2(line.substring(3).trim()));
      continue;
    }
    if (line.startsWith('### ')) {
      children.push(H3(line.substring(4).trim()));
      continue;
    }

    // Tables - simplified handling
    if (line.startsWith('|') && line.includes('---')) {
      continue; // skip separator
    }
    if (line.startsWith('| ') && !inTable) {
      inTable = true;
      tableHeaders = line.split('|').filter(c => c.trim()).map(c => c.trim());
      continue;
    }
    if (line.startsWith('| ') && inTable) {
      const cells = line.split('|').filter(c => c.trim()).map(c => c.trim());
      if (cells.length === tableHeaders.length) {
        tableRows.push(cells);
      } else {
        // End of table
        if (tableRows.length > 0) {
          children.push(makeTable(tableHeaders, tableRows));
        }
        tableHeaders = [];
        tableRows = [];
        inTable = false;
        // Process as normal paragraph
        children.push(P(line.replace(/^\| /, '').replace(/\|$/, '').trim()));
      }
      continue;
    }
    if (inTable && !line.startsWith('|')) {
      if (tableRows.length > 0) {
        children.push(makeTable(tableHeaders, tableRows));
      }
      tableHeaders = [];
      tableRows = [];
      inTable = false;
    }

    // Horizontal rule
    if (line.trim() === '---') {
      children.push(new Paragraph({ border: { bottom: BR }, children: [] }));
      continue;
    }

    // Page break hint
    if (line.includes('[PageBreak]') || line.includes('<!-- pagebreak -->')) {
      children.push(new Paragraph({ children: [new PageBreak()] }));
      continue;
    }

    // Regular paragraph (strip markdown)
    let text = line
      .replace(/^\s*\*\*(.+?)\*\*\s*$/, '$1') // bold
      .replace(/`([^`]+)`/g, '$1')           // inline code
      .replace(/\*\*(.+?)\*\*/g, '$1')         // bold
      .replace(/\*(.+?)\*/g, '$1');             // italic
    
    if (text.trim()) {
      children.push(P(text));
    }
  }

  // Flush any remaining table
  if (inTable && tableRows.length > 0) {
    children.push(makeTable(tableHeaders, tableRows));
  }

  const doc = new Document({
    styles: {
      default: { document: { run: { font: 'Microsoft YaHei', size: 22 } } },
      paragraphStyles: [
        { id: 'Heading1', name: 'Heading 1', basedOn: 'Normal', next: 'Normal', quickFormat: true,
          run: { size: 36, bold: true, font: 'Microsoft YaHei', color: '1a365d' },
          paragraph: { spacing: { before: 400, after: 200 }, outlineLevel: 0 } },
        { id: 'Heading2', name: 'Heading 2', basedOn: 'Normal', next: 'Normal', quickFormat: true,
          run: { size: 30, bold: true, font: 'Microsoft YaHei', color: '2c5282' },
          paragraph: { spacing: { before: 300, after: 150 }, outlineLevel: 1 } },
        { id: 'Heading3', name: 'Heading 3', basedOn: 'Normal', next: 'Normal', quickFormat: true,
          run: { size: 26, bold: true, font: 'Microsoft YaHei', color: '3182ce' },
          paragraph: { spacing: { before: 240, after: 120 }, outlineLevel: 2 } },
      ]
    },
    sections: [{
      properties: { page: { size: { width: 12240, height: 15840 }, margin: { top: 1440, right: 1440, bottom: 1440, left: 1440 } } },
      headers: { default: new Header({ children: [new Paragraph({ alignment: AlignmentType.RIGHT, children: [new TextRun({ text: 'warm-flow-ui NPM \u6539\u9020\u5b9e\u65bd\u65b9\u6848', size: 18, color: '888888' })] })] }) },
      footers: { default: new Footer({ children: [new Paragraph({ alignment: AlignmentType.CENTER, children: [new TextRun({ text: '\u7B2C ', size: 18 }), new TextRun({ children: [PageNumber.CURRENT], size: 18 }), new TextRun({ text: ' \u9875', size: 18 })] })] }) },
      children
    }]
  });

  const buffer = await Packer.toBuffer(doc);
  const outputPath = path.join(__dirname, 'WarmFlow-NPM改造完整实施方案.docx');
  fs.writeFileSync(outputPath, buffer);
  console.log(`\u2705 Word \u6587\u6863\u5df2\u751f\u6210: ${outputPath}`);
  console.log(`   \u5927\u5c0f: ${(buffer.length / 1024).toFixed(1)} KB`);
}

main().catch(console.error);
