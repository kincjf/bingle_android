package net.sourceforge.opencamera.Preview;

public interface JSONCommandInterface {

	//네트워크 전송 관련 key
	final String STATUS="status";//네트워크 상태를 받아온다 -> 200:ok, 404:page Not found ...
	final String WEB_FILE_DIR = "image"; //서버에서의 도메인 이하 파일의 경로를 의미함 ex) file/219312.jpg
	final String CONNECT_URL = "url"; //연결할 서버의 url을 의미한다

	//네트워크 전송 관련 value
	final int STATUS_404=404;//STATUS의 상태를 표시한다
	final int STATUS_OK=200;//STATUS의 상태를 표시한다


	//AsyncTask 관리 key
	final String COMMAND="command";//doInBackground 에서 수행할 업무내용을 담는다
	final String FILE_PATH="file_path";//사용될 파일의 경로를 담는다

	//AsyncTask 관리 value
	final String CMD_UPLOAD="upload";//COMMAND에 해당하는 값들을 의미한다
	final String CMD_DOWNLOAD="download";//COMMAND에 해당하는 값들을 의미한다




}
