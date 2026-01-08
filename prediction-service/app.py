# prediction_service/app.py
from flask import Flask, request, jsonify
from flask_cors import CORS
from datetime import datetime

# Simula o carregamento do modelo e a lógica de Feature Engineering
app = Flask(__name__)
CORS(app) # Importante para permitir requisições do frontend/backend Java

# Adicione isso no seu app.py (Python)
@app.route('/', methods=['GET'])
def health_check():
    return {"status": "online"}, 200

# --- LÓGICA DO MODELO (Time de Data Science) ---
def predict_risk(data):
    """ Calcula a probabilidade de atraso com base nos dados de entrada. """
    try:
        # Extração de Features: Data Science transforma o datetime em features numéricas
        data_partida_str = data.get('data_partida')
        distancia = data.get('distancia_km', 0)
        origem = data.get('origem', '').upper()
        companhia = data.get('companhia', '').upper()
        
        # Engenharia de Features (Ex: transformar a data em hora e dia da semana)
        data_partida = datetime.strptime(data_partida_str, "%Y-%m-%dT%H:%M:%S")
        hour_of_day = data_partida.hour
        day_of_week = data_partida.weekday() # 0=Segunda, 6=Domingo
        
        # 2. Cálculo de Risco (Simulação do Modelo ML)
        risk_score = 0.1 # Risco Base
        
        # Regra 1: Horário de Pico (Atraso mais provável das 17h às 20h)
        if 17 <= hour_of_day <= 20:
            risk_score += 0.35
            
        # Regra 2: Dia Crítico (Fim de semana/segunda)
        if day_of_week in [0, 4, 6]: # Seg, Sex, Dom
            risk_score += 0.15

        # Regra 3: Rota Crítica (Ex: Ponte Aérea)
        if origem == "GIG" and data.get('destino', '').upper() == "GRU" and distancia < 400:
            risk_score += 0.40
        
        # Final probability clamp (0.0 to 1.0)
        probability = min(1.0, risk_score)
        
        # 3. Decisão
        if probability >= 0.6: 
            previsao = "Atrasado"
        else:
            previsao = "Pontual"
            
        return previsao, probability

    except Exception as e:
        print(f"Erro no processamento de dados do modelo: {e}")
        return "Erro Interno do Serviço DS", 0.0

@app.route('/predict_internal', methods=['POST'])
def predict():
    data = request.get_json()
    if not data:
        return jsonify({"message": "Nenhum dado recebido"}), 400
        
    previsao, probability = predict_risk(data)

    if "Erro" in previsao:
        return jsonify({"previsao": previsao, "probabilidade": 0.0}), 500
        
    # Retorno no formato solicitado pelo desafio (probabilidade como float)
    return jsonify({
        "previsao": previsao,
        "probabilidade": round(probability, 4)
    })

if __name__ == '__main__':
    # host='0.0.0.0' diz ao Flask para aceitar chamadas de fora do container (do seu Java)
    app.run(host='0.0.0.0', port=5000)