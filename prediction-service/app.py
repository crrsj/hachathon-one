# prediction_service/app.py
from flask import Flask, request, jsonify
from flask_cors import CORS
from datetime import datetime
from flasgger import Swagger

app = Flask(__name__)
CORS(app)
swagger = Swagger(app)

@app.route('/', methods=['GET'])
def health_check():
    """
    Endpoint de verificação de saúde da API
    ---
    responses:
      200:
        description: API está online
    """
    return {"status": "online"}, 200


def predict_risk(data):
    try:
        data_partida_str = data.get('data_partida')
        if not data_partida_str:
            return "Erro: data_partida ausente", 0.0

        distancia = data.get('distancia_km', 0)
        origem = data.get('origem', '').upper()
        

        data_partida = datetime.strptime(data_partida_str, "%Y-%m-%dT%H:%M:%S")
        hour_of_day = data_partida.hour
        day_of_week = data_partida.weekday() 
        
        risk_score = 0.1 
        if 17 <= hour_of_day <= 20: risk_score += 0.35
        if day_of_week in [0, 4, 6]: risk_score += 0.15
        if origem == "GIG" and data.get('destino', '').upper() == "GRU" and distancia < 400:
            risk_score += 0.40
        
        probability = min(1.0, risk_score)
        previsao = "Atrasado" if probability >= 0.6 else "Pontual"
        return previsao, probability

    except Exception as e:
        print(f"Erro no processamento: {e}")
        return f"Erro: {str(e)}", 0.0

@app.route('/predict_internal', methods=['POST'])
def predict():
    """
    Endpoint de Predição de Risco de Voo
    ---
    parameters:
      - name: body
        in: body
        required: true
        schema:
          properties:
            data_partida:
              type: string
              example: "2025-10-15T18:30:00"
            distancia_km:
              type: integer
              example: 380
            origem:
              type: string
              example: "GIG"
            destino:
              type: string
              example: "GRU"
    responses:
      200:
        description: Sucesso na predição
    """
    data = request.get_json()
    if not data:
        return jsonify({"message": "Nenhum dado recebido"}), 400
        
    previsao, probability = predict_risk(data)

    if "Erro" in previsao:
        return jsonify({"previsao": previsao, "probabilidade": 0.0}), 500
        
    return jsonify({
        "previsao": previsao, 
        "probabilidade": round(probability, 2),
        "timestamp": datetime.now().isoformat()
    }), 200


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)