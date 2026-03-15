from fastapi import FastAPI, UploadFile, File, Form
from fastapi.middleware.cors import CORSMiddleware
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
import docx
import PyPDF2
import tempfile

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

def extract_text_from_docx(file_path):
    doc = docx.Document(file_path)
    return " ".join([p.text for p in doc.paragraphs])

def extract_text_from_pdf(file_path):
    reader = PyPDF2.PdfReader(file_path)
    text = ""
    for page in reader.pages:
        text += page.extract_text()
    return text


@app.post("/analyze")
async def analyze_resume(
        job_description: str = Form(...),
        resume: UploadFile = File(...)
):

    # save temp file
    with tempfile.NamedTemporaryFile(delete=False) as tmp:
        content = await resume.read()
        tmp.write(content)
        temp_path = tmp.name

    if resume.filename.endswith(".docx"):
        resume_text = extract_text_from_docx(temp_path)
    else:
        resume_text = extract_text_from_pdf(temp_path)

    documents = [resume_text, job_description]

    vectorizer = TfidfVectorizer(stop_words="english")
    vectors = vectorizer.fit_transform(documents)

    similarity = cosine_similarity(vectors[0:1], vectors[1:2])
    score = float(similarity[0][0] * 100)

    return {"similarity_score": round(score, 2)}