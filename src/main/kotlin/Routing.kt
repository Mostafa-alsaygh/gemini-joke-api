package com.example

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.http.content.*
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.UUID

fun Application.configureRouting() {
    routing {

        staticResources("/content", "mycontent")

        get("/") {
            val htmlContent = """
                <!DOCTYPE html>
                <html lang="ar" dir="rtl">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>مولد النكات العراقية - ذكاء اصطناعي</title>
                    <link rel="preconnect" href="https://fonts.googleapis.com">
                    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
                    <link href="https://fonts.googleapis.com/css2?family=Cairo:wght@400;600;700;800&family=Outfit:wght@500;700&display=swap" rel="stylesheet">
                    <style>
                        :root {
                            --bg-dark: #07070a;
                            --bg-card: rgba(15, 15, 24, 0.7);
                            --primary: #9333ea;
                            --primary-light: #a855f7;
                            --primary-glow: rgba(147, 51, 234, 0.35);
                            --accent: #6366f1;
                            --text-main: #f3f4f6;
                            --text-muted: #9ca3af;
                            --border: rgba(255, 255, 255, 0.08);
                            --cairo: 'Cairo', sans-serif;
                            --outfit: 'Outfit', sans-serif;
                        }
                
                        * {
                            box-sizing: border-box;
                            margin: 0;
                            padding: 0;
                        }
                
                        body {
                            font-family: var(--cairo);
                            background-color: var(--bg-dark);
                            background-image: 
                                radial-gradient(circle at 50% 0%, rgba(99, 102, 241, 0.15) 0%, transparent 60%),
                                radial-gradient(circle at 10% 100%, rgba(147, 51, 234, 0.08) 0%, transparent 40%);
                            color: var(--text-main);
                            min-height: 100vh;
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            padding: 20px;
                            overflow-x: hidden;
                        }
                
                        .container {
                            width: 100%;
                            max-width: 540px;
                            background: var(--bg-card);
                            border: 1px solid var(--border);
                            border-radius: 28px;
                            padding: 40px 30px;
                            backdrop-filter: blur(20px);
                            -webkit-backdrop-filter: blur(20px);
                            box-shadow: 0 24px 60px rgba(0, 0, 0, 0.5), 
                                        inset 0 1px 0 rgba(255, 255, 255, 0.1);
                            text-align: center;
                            transition: all 0.3s ease;
                            position: relative;
                        }
                
                        .container::before {
                            content: '';
                            position: absolute;
                            top: 0;
                            left: 50%;
                            transform: translateX(-50%);
                            width: 250px;
                            height: 1px;
                            background: linear-gradient(90deg, transparent, rgba(168, 85, 247, 0.5), transparent);
                        }
                
                        .logo-area {
                            margin-bottom: 28px;
                        }
                
                        .sparkle-badge {
                            display: inline-flex;
                            align-items: center;
                            gap: 6px;
                            background: rgba(147, 51, 234, 0.12);
                            border: 1px solid rgba(147, 51, 234, 0.25);
                            color: #d8b4fe;
                            padding: 6px 16px;
                            border-radius: 100px;
                            font-size: 0.85rem;
                            font-weight: 700;
                            margin-bottom: 16px;
                        }
                
                        h1 {
                            font-size: 2.2rem;
                            font-weight: 800;
                            letter-spacing: -0.01em;
                            background: linear-gradient(135deg, #ffffff 30%, #e9d5ff 100%);
                            -webkit-background-clip: text;
                            -webkit-text-fill-color: transparent;
                            margin-bottom: 8px;
                        }
                
                        .desc {
                            color: var(--text-muted);
                            font-size: 0.95rem;
                            line-height: 1.6;
                            margin-bottom: 32px;
                        }
                
                        .input-box {
                            position: relative;
                            margin-bottom: 20px;
                            text-align: right;
                        }
                
                        .input-label {
                            display: block;
                            font-size: 0.85rem;
                            font-weight: 700;
                            color: var(--text-muted);
                            margin-bottom: 8px;
                            padding-right: 4px;
                        }
                
                        .input-wrapper {
                            position: relative;
                            display: flex;
                            align-items: center;
                        }
                
                        .input-element {
                            width: 100%;
                            padding: 16px 20px 16px 50px;
                            background: rgba(0, 0, 0, 0.2);
                            border: 1.5px solid var(--border);
                            border-radius: 18px;
                            color: #ffffff;
                            font-size: 1.05rem;
                            font-family: var(--cairo);
                            font-weight: 600;
                            outline: none;
                            transition: all 0.25s ease;
                            text-align: right;
                        }
                
                        .input-element:focus {
                            border-color: var(--primary-light);
                            box-shadow: 0 0 0 4px var(--primary-glow);
                            background: rgba(0, 0, 0, 0.35);
                        }
                
                        .input-element::placeholder {
                            color: #4b5563;
                        }
                
                        .input-icon {
                            position: absolute;
                            left: 20px;
                            color: var(--text-muted);
                            font-size: 1.2rem;
                            transition: color 0.25s ease;
                            pointer-events: none;
                        }
                
                        .input-element:focus ~ .input-icon {
                            color: var(--primary-light);
                        }
                
                        .btn-submit {
                            width: 100%;
                            padding: 16px 24px;
                            background: linear-gradient(135deg, var(--primary), var(--accent));
                            border: none;
                            border-radius: 18px;
                            color: white;
                            font-family: var(--cairo);
                            font-size: 1.1rem;
                            font-weight: 700;
                            cursor: pointer;
                            transition: all 0.25s ease;
                            box-shadow: 0 4px 20px var(--primary-glow);
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            gap: 10px;
                        }
                
                        .btn-submit:hover:not(:disabled) {
                            transform: translateY(-2px);
                            box-shadow: 0 8px 30px rgba(147, 51, 234, 0.5);
                            filter: brightness(1.1);
                        }
                
                        .btn-submit:active:not(:disabled) {
                            transform: translateY(1px);
                        }
                
                        .btn-submit:disabled {
                            opacity: 0.6;
                            cursor: not-allowed;
                        }
                
                        .loading-shimmer {
                            display: none;
                            margin-top: 24px;
                            background: rgba(255, 255, 255, 0.02);
                            border: 1px solid var(--border);
                            border-radius: 22px;
                            padding: 24px;
                        }
                
                        .shimmer-row {
                            height: 16px;
                            background: linear-gradient(90deg, rgba(255, 255, 255, 0.02) 25%, rgba(255, 255, 255, 0.06) 50%, rgba(255, 255, 255, 0.02) 75%);
                            background-size: 200% 100%;
                            animation: shimmerEffect 1.5s infinite linear;
                            border-radius: 8px;
                            margin-bottom: 12px;
                        }
                
                        .shimmer-row:last-child {
                            margin-bottom: 0;
                            width: 70%;
                            margin-right: auto;
                            margin-left: auto;
                        }
                
                        @keyframes shimmerEffect {
                            0% { background-position: 200% 0; }
                            100% { background-position: -200% 0; }
                        }
                
                        .result-card {
                            display: none;
                            margin-top: 28px;
                            background: rgba(255, 255, 255, 0.015);
                            border: 1px solid var(--border);
                            border-radius: 22px;
                            padding: 28px;
                            text-align: right;
                            position: relative;
                            animation: popIn 0.45s cubic-bezier(0.16, 1, 0.3, 1) forwards;
                            box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.05);
                        }
                
                        @keyframes popIn {
                            from {
                                opacity: 0;
                                transform: translateY(15px) scale(0.98);
                            }
                            to {
                                opacity: 1;
                                transform: translateY(0) scale(1);
                            }
                        }
                
                        .result-meta {
                            display: flex;
                            justify-content: space-between;
                            align-items: center;
                            margin-bottom: 18px;
                            font-size: 0.85rem;
                            color: var(--text-muted);
                        }
                
                        .tag-word {
                            background: rgba(147, 51, 234, 0.15);
                            color: #d8b4fe;
                            padding: 4px 12px;
                            border-radius: 8px;
                            font-weight: 700;
                        }
                
                        .joke-content {
                            font-size: 1.3rem;
                            line-height: 1.8;
                            color: #ffffff;
                            font-weight: 600;
                            margin-bottom: 24px;
                            white-space: pre-wrap;
                        }
                
                        .result-actions {
                            display: flex;
                            gap: 12px;
                            justify-content: flex-start;
                        }
                
                        .action-button {
                            background: rgba(255, 255, 255, 0.04);
                            border: 1px solid var(--border);
                            border-radius: 12px;
                            color: var(--text-main);
                            padding: 10px 18px;
                            font-size: 0.9rem;
                            font-family: var(--cairo);
                            font-weight: 600;
                            cursor: pointer;
                            display: inline-flex;
                            align-items: center;
                            gap: 8px;
                            transition: all 0.2s;
                        }
                
                        .action-button:hover {
                            background: rgba(255, 255, 255, 0.08);
                            border-color: rgba(255, 255, 255, 0.15);
                            color: #ffffff;
                        }
                
                        .action-button:active {
                            transform: scale(0.97);
                        }
                
                        .error-alert {
                            display: none;
                            margin-top: 24px;
                            background: rgba(239, 68, 68, 0.08);
                            border: 1px solid rgba(239, 68, 68, 0.2);
                            border-radius: 16px;
                            padding: 16px;
                            color: #fca5a5;
                            font-size: 0.95rem;
                            align-items: center;
                            justify-content: center;
                            gap: 8px;
                        }
                
                        .toast-popup {
                            position: fixed;
                            bottom: 30px;
                            left: 50%;
                            transform: translateX(-50%) translateY(100px);
                            background: #121218;
                            border: 1px solid var(--border);
                            color: var(--text-main);
                            padding: 12px 24px;
                            border-radius: 14px;
                            font-size: 0.9rem;
                            font-weight: 700;
                            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.6);
                            transition: transform 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
                            z-index: 999;
                            display: flex;
                            align-items: center;
                            gap: 8px;
                            direction: rtl;
                        }
                
                        .toast-popup.visible {
                            transform: translateX(-50%) translateY(0);
                        }
                
                        @media (max-width: 480px) {
                            .container {
                                padding: 30px 20px;
                            }
                            h1 {
                                font-size: 1.8rem;
                            }
                        }
                    </style>
                </head>
                <body>
                
                    <div class="container">
                        <div class="logo-area">
                            <div class="sparkle-badge">
                                <span>✨</span>
                                <span>بواسطة Gemini 3.5 Flash</span>
                            </div>
                            <h1>نكت عراقية</h1>
                            <p class="desc">أدخل أي موضوع أو كلمة ترغب بها، وسيقوم الذكاء الاصطناعي بتوليد نكتة عراقية كوميدية مخصصة لك!</p>
                        </div>
                
                        <div class="input-box">
                            <label class="input-label" for="topic-input">موضوع النكتة (مثال: دراسة، حر، ريجيم، سفر...)</label>
                            <div class="input-wrapper">
                                <input type="text" id="topic-input" class="input-element" placeholder="اكتب شيئاً مضحكاً..." autocomplete="off">
                                <span class="input-icon">🎭</span>
                            </div>
                        </div>
                
                        <button id="btn-submit" class="btn-submit">
                            <span>ولد نكتة! 🚀</span>
                        </button>
                
                        <div id="loader" class="loading-shimmer">
                            <div class="shimmer-row"></div>
                            <div class="shimmer-row"></div>
                            <div class="shimmer-row"></div>
                        </div>
                
                        <div id="result-card" class="result-card">
                            <div class="result-meta">
                                <span id="tag-word" class="tag-word">#موضوع</span>
                                <span>😂 النكتة جاهزة</span>
                            </div>
                            <div id="joke-content" class="joke-content"></div>
                            <div class="result-actions">
                                <button id="btn-copy" class="action-button">
                                    <span>نسخ النكتة 📋</span>
                                </button>
                                <button id="btn-share" class="action-button">
                                    <span>مشاركة 🔗</span>
                                </button>
                            </div>
                        </div>
                
                        <div id="error-alert" class="error-alert">
                            <span>⚠️</span>
                            <span id="error-message">حدث خطأ ما، يرجى المحاولة لاحقاً.</span>
                        </div>
                    </div>
                
                    <div id="toast" class="toast-popup">
                        <span class="toast-text">تم النسخ بنجاح!</span>
                    </div>
                
                    <script>
                        const inputField = document.getElementById('topic-input');
                        const submitBtn = document.getElementById('btn-submit');
                        const loader = document.getElementById('loader');
                        const resultCard = document.getElementById('result-card');
                        const jokeContent = document.getElementById('joke-content');
                        const tagWord = document.getElementById('tag-word');
                        const errorAlert = document.getElementById('error-alert');
                        const errorMessage = document.getElementById('error-message');
                        const copyBtn = document.getElementById('btn-copy');
                        const shareBtn = document.getElementById('btn-share');
                
                        async function fetchJoke() {
                            const word = inputField.value.trim();
                            if (!word) {
                                showToast("يرجى كتابة موضوع أولاً!");
                                return;
                            }
                
                            resultCard.style.display = 'none';
                            errorAlert.style.display = 'none';
                            loader.style.display = 'block';
                            submitBtn.disabled = true;
                
                            try {
                                const response = await fetch('/thechancejoks/joke?word=' + encodeURIComponent(word));
                                const data = await response.json();
                
                                if (!response.ok || data.error) {
                                    throw new Error(data.error || "فشل الاتصال بالخادم لتوليد النكتة.");
                                }
                
                                jokeContent.textContent = data.joke;
                                tagWord.textContent = '#' + data.word;
                                loader.style.display = 'none';
                                resultCard.style.display = 'block';
                            } catch (err) {
                                loader.style.display = 'none';
                                errorMessage.textContent = err.message || "حدث خطأ غير متوقع.";
                                errorAlert.style.display = 'flex';
                            } finally {
                                submitBtn.disabled = false;
                            }
                        }
                
                        submitBtn.addEventListener('click', fetchJoke);
                
                        inputField.addEventListener('keypress', (e) => {
                            if (e.key === 'Enter') {
                                fetchJoke();
                            }
                        });
                
                        copyBtn.addEventListener('click', () => {
                            const text = jokeContent.textContent;
                            navigator.clipboard.writeText(text).then(() => {
                                showToast("✨ تم نسخ النكتة بنجاح!");
                            }).catch(() => {
                                showToast("❌ فشل النسخ.");
                            });
                        });
                
                        shareBtn.addEventListener('click', () => {
                            const joke = jokeContent.textContent;
                            if (navigator.share) {
                                navigator.share({
                                    title: 'نكتة عراقية مضحكة',
                                    text: joke,
                                    url: window.location.href
                                }).catch(() => {});
                            } else {
                                navigator.clipboard.writeText(joke + "\n\nالمزيد من النكات: " + window.location.href).then(() => {
                                    showToast("🔗 تم نسخ النكتة ورابط الموقع للمشاركة!");
                                });
                            }
                        });
                
                        function showToast(message) {
                            const toast = document.getElementById('toast');
                            toast.querySelector('.toast-text').textContent = message;
                            toast.classList.add('visible');
                            setTimeout(() => {
                                toast.classList.remove('visible');
                            }, 2500);
                        }
                    </script>
                </body>
                </html>
            """.trimIndent()
            call.respondText(htmlContent, ContentType.Text.Html.withCharset(Charsets.UTF_8))
        }

        get("/thechancejoks/joke") {
            val word = call.request.queryParameters["word"]
            if (word.isNullOrBlank()) {
                call.respondText(
                    "{\"error\":\"Missing query parameter 'word'. Usage: /thechancejoks/joke?word=topic\"}",
                    ContentType.Application.Json.withCharset(Charsets.UTF_8),
                    HttpStatusCode.BadRequest
                )
                return@get
            }

            try {
                val joke = generateIraqiJoke(word)
                val escapedJoke = escapeJsonString(joke).removeSurrounding("\"")
                val responseJson = """{"word":"$word","joke":"$escapedJoke"}"""
                call.respondText(
                    responseJson,
                    ContentType.Application.Json.withCharset(Charsets.UTF_8),
                    HttpStatusCode.OK
                )
            } catch (e: Exception) {
                val errorMsg = escapeJsonString(e.message ?: "Unknown error").removeSurrounding("\"")
                call.respondText(
                    """{"error":"Failed to generate joke: $errorMsg"}""",
                    ContentType.Application.Json.withCharset(Charsets.UTF_8),
                    HttpStatusCode.InternalServerError
                )
            }
        }
    }
}
//AQ.Ab8RN6IUVJ-2A8_hLpYZFnbJ0XTw0_Y7nMMKiMG5bvb2d7ZhEA
private fun generateIraqiJoke(word: String): String {
    val apiKey = System.getenv("GEMINI_API_KEY") ?: "AQ.Ab8RN6IUVJ-2A8_hLpYZFnbJ0XTw0_Y7nMMKiMG5bvb2d7ZhEA"
    val randomId = UUID.randomUUID().toString()

    val prompt = """
        Generate a funny, clean joke in the Iraqi Arabic dialect (using Iraqi slang and cultural references, written in Arabic script) about the topic: '$word'.
        To ensure variety and that it is different every time, use this unique random seed: '$randomId'.
        Do not repeat jokes. Make it extremely funny, creative, and lighthearted. Do not include any racism, hate speech, or offensive content.
        Respond only with the joke itself, no introductory text, no quotes, no markdown formatting.
    """.trimIndent()

    val requestBody = """
        {
          "contents": [{
            "parts": [{
              "text": ${escapeJsonString(prompt)}
            }]
          }],
          "generationConfig": {
            "temperature": 1.0
          }
        }
    """.trimIndent()

    val client = HttpClient.newHttpClient()
    val request = HttpRequest.newBuilder()
        .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
        .build()

    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
    if (response.statusCode() != 200) {
        throw RuntimeException("Gemini API error (Status ${response.statusCode()}): ${response.body()}")
    }

    return extractTextFromGeminiResponse(response.body())
}

private fun escapeJsonString(value: String): String {
    val builder = StringBuilder()
    builder.append("\"")
    for (char in value) {
        when (char) {
            '\\' -> builder.append("")
            '\"' -> builder.append("")
            '\n' -> builder.append("")
            '\r' -> builder.append("")
            '\t' -> builder.append("")
            else -> {
                if (char.code < 0x20) {
                    builder.append(String.format("\\u%04x", char.code))
                } else {
                    builder.append(char)
                }
            }
        }
    }
    builder.append("\"")
    return builder.toString()
}

private fun extractTextFromGeminiResponse(response: String): String {
    val textIndex = response.indexOf("\"text\":")
    if (textIndex == -1) return "Could not generate a joke right now, try again!"
    val startQuoteIndex = response.indexOf("\"", textIndex + 7)
    if (startQuoteIndex == -1) return "Could not generate a joke right now, try again!"

    val sb = StringBuilder()
    var i = startQuoteIndex + 1
    while (i < response.length) {
        val char = response[i]
        if (char == '\\') {
            if (i + 1 < response.length) {
                val nextChar = response[i + 1]
                when (nextChar) {
                    'n' -> sb.append('\n')
                    'r' -> sb.append('\r')
                    't' -> sb.append('\t')
                    '\"' -> sb.append('\"')
                    '\\' -> sb.append('\\')
                    else -> sb.append(nextChar)
                }
                i += 2
                continue
            }
        } else if (char == '\"') {
            break
        } else {
            sb.append(char)
        }
        i++
    }
    return sb.toString().trim()
}