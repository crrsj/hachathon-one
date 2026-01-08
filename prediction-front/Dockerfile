# Usa o servidor Nginx para servir os arquivos
FROM nginx:alpine

# Copia todos os arquivos da pasta atual para o diretório do Nginx
COPY . /usr/share/nginx/html

# Expõe a porta 80 (padrão do Nginx)
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]