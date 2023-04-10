ports=(9000 9001 9002 9003 9004)

for port in ${ports[@]}; do
  pid=$(lsof -ti :$port)
  if [ ! -z "$pid" ]; then
    echo "Killing process on port $port with PID $pid"
    kill -9 $pid
  else
    echo "No process found on port $port"
  fi
done