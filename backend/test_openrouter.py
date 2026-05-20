import urllib.request, json

url = "https://openrouter.ai/api/v1/chat/completions"
payload = json.dumps({
    "model": "qwen/qwen3-32b:free",
    "messages": [
        {"role": "system", "content": "Answer in Chinese, under 50 chars."},
        {"role": "user", "content": "hello"}
    ],
    "max_tokens": 4096,
    "temperature": 0.7
}).encode("utf-8")

req = urllib.request.Request(url, data=payload, headers={
    "Content-Type": "application/json",
    "Authorization": "Bearer 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",
    "HTTP-Referer": "http://localhost:5173",
    "X-Title": "DiabetesHealthSystem"
})

try:
    with urllib.request.urlopen(req, timeout=30) as resp:
        data = json.loads(resp.read().decode("utf-8"))
        print("SUCCESS")
        print(json.dumps(data, ensure_ascii=False, indent=2))
except urllib.error.HTTPError as e:
    print("HTTP_ERROR", e.code)
    body = e.read().decode("utf-8")
    print(body)
except Exception as e:
    print("NETWORK_ERROR")
    print(str(e))
