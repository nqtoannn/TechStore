from flask import Flask, request, jsonify
from tensorflow.keras.models import load_model
import pickle
from tensorflow.keras.preprocessing import sequence
import numpy as np

# Load model and tokenizer
model = load_model("model/Text_CNN_model_v13.keras")
with open('tokenizer/tokenizer.pickle', 'rb') as handle:
    tokenizer = pickle.load(handle)

app = Flask(__name__)

@app.route('/predict', methods=['POST'])
def predict():
    data = request.json
    text = data['text']
    
    # Preprocess input
    seq = tokenizer.texts_to_sequences([text])
    padded_seq = sequence.pad_sequences(seq, maxlen=100)

    # Predict
    prediction = model.predict(padded_seq)
    label = np.argmax(prediction)
    # Return result
    labels = ["clean", "offensive", "hate"]
    return jsonify({"label": labels[label], "confidence": float(np.max(prediction))})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
