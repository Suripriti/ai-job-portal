from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
import docx
import os

app = FastAPI()

# Enable CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# Extract text from DOCX
def extract_text_from_docx(path):
    doc = docx.Document(path)
    text = [p.text for p in doc.paragraphs]
    return " ".join(text)


@app.post("/score")
def calculate_score(data: dict):

    resume_path = data.get("resumePath")
    job_description = data.get("jobDescription")

    if not resume_path or not os.path.exists(resume_path):
        return {"score": 0, "error": "Resume file not found"}

    try:
        resume_text = extract_text_from_docx(resume_path)
    except:
        return {"score": 0, "error": "Failed to read resume"}

    # TF-IDF Similarity
    documents = [resume_text, job_description]

    vectorizer = TfidfVectorizer(stop_words="english")
    vectors = vectorizer.fit_transform(documents)

    similarity = cosine_similarity(vectors[0:1], vectors[1:2])

    score = float(similarity[0][0] * 100)

    return {
        "score": round(score, 2)
    }