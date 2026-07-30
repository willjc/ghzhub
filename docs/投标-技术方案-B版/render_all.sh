#!/bin/bash
cd "$(dirname "$0")" || exit 1
mkdir -p images
cat > /tmp/mmdc_cfg.json <<'JSON'
{ "theme": "default", "flowchart": {"htmlLabels": true, "useMaxWidth": true} }
JSON
ok=0; fail=0
for f in mermaid/B*.mmd; do
  name=$(basename "$f" .mmd)
  if npx -y -p @mermaid-js/mermaid-cli mmdc -i "$f" -o "images/${name}.png" -b white -c /tmp/mmdc_cfg.json -s 2 >/dev/null 2>&1; then
    echo "OK  ${name}"
    ok=$((ok+1))
  else
    echo "FAIL ${name}"
    fail=$((fail+1))
  fi
done
echo "==== 成功:${ok} 失败:${fail} ===="
ls -la images/B*.png 2>/dev/null | wc -l
