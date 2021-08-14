
/* ---------------------------------------------
## 목차
1. 개체의 기본형
2.* 개체에 관련된 데이터 저장소
2.1. LevelData
3.* 개체의 동작에 필요한 클래스
3.1.파일 입출력 관련 함수

----------------------------------------------- */
//
//개체에 기본형을 만든 이유는 기본형을 상속한 여러 성격의 객체를 구현할 계획이 있기 때문이다.
// ex) 덤벙대는 개체, 예민한 개체, 우두머리 개체 등..
//
//
//idea) input 이 첫 레벨에서 이루어지는게 아니라 회로의 중간에서도 이루어질 수 있다면?
//re-idea) 차라리 레벨이 연속적으로(0->1->2->3) 이어지는게 아니라 유기적으로 이어지게 한다면? (0->2->4)
// 도전과제) 역행구현 *고려1: 무한순환 방지 (아웃풋 노드까지 경로 길이 계산 구현) 무한일 경우 센티넬 개입 (경로 파괴, 경로 조작, 구간반복 해제)
// 도전과제2) 전달 속도 다양화 (빠른 생각, 느린 생각)
//re-re-idea) level0는 인풋으로 고정! -> 자극 전달을 레벨과 무관하게 진행되도록
//
//
//

import java.util.*;
import java.io.*;
import static java.lang.System.exit;
import java.util.logging.Level;
/*-------------------------------1.개체의 기본형-------------------------------*/

public class Entity {
    private HashMap<LevelData, HashMap<Node, HashSet<Edge>>> mindCircuit; // (levelData -(Node - Edge))의 구성.
    private HashSet<Node> previousSparkedNode = new HashSet<>();
    private HashSet<Node> currentSparkedNode = new HashSet<>();
    ArrayList<String> inputFileList;
    ArrayList<String> outputFileList;

    Entity(ArrayList<String> inputFileList, ArrayList<String> outputFileList){
        this.inputFileList = inputFileList;
        this.outputFileList = outputFileList;
        mindCircuit =  Matcher(readNodeFromFile(inputFileList.get(0)), readEdgeFromFile(inputFileList.get(1)));
        HashMap<Integer, HashSet<Edge>> tempEdgeMap = readEdgeFromFile(inputFileList.get(1));
        /*HashMap<LevelData, HashMap> */
    }
    //Entity(){};





    private HashMap<LevelData, HashSet<Node>> readNodeFromFile(String inputFile){// Node 만 읽어 ArrayList 로 저장, level 과 묶어 map 을 만들어 반환.
        HashMap<LevelData, HashSet<Node>> result = new HashMap<>();
        LevelData levelData = null;
        HashSet<Node> nodeData = null;
        String fileLine = "";
        ArrayList<String> splitInputData = null;
        try {
            FileReader fileInput = new FileReader(inputFile);
            BufferedReader inputBuffer = new BufferedReader(fileInput);
            for (int i = 1; (fileLine = inputBuffer.readLine()) != null; i++){  /// 파일에서 라인 읽어오기
                if(fileLine.trim().startsWith("$$"))    //라인의 가장 앞에 나오는 $$는 주석역할
                    continue;
                if(fileLine.trim().startsWith("##")) {    //라인의 가장 첫번째에 오는 ## 새로운 레벨에 속하는 노드의 입력임을 나타냄.
                    fileLine = fileLine.replace("#", "");
                    fileLine = fileLine.replace("[", "");
                    splitInputData = new ArrayList<String>(Arrays.stream(fileLine.split("]")).toList());
                    levelData = makeLevelDataFromAList(splitInputData);
                    nodeData = new HashSet<Node>();
                    result.put(levelData, nodeData);
                    continue;
                }
                fileLine = fileLine.replace("[", "");
                splitInputData = new ArrayList<String>(Arrays.stream(fileLine.split("]")).toList());
                assert nodeData != null;        // 컴파일러가 처리한 코드. assert 명령어 공부 필요.
                nodeData.add(makeNodeFromAList(splitInputData));
            }
        } catch (IOException ie){
            ie.printStackTrace();
        }
        return result;
    }

    private LevelData makeLevelDataFromAList(ArrayList<String> data){
        LevelData levelData = null;
        levelData = new LevelData(Integer.parseInt(data.get(0).trim()), Integer.parseInt(data.get(1).trim()), data.get(2).trim());
        return levelData;
    }

    private Node makeNodeFromAList(ArrayList<String> data){
        Node node = null;
        int nodeFormat = Integer.parseInt(data.get(0).trim());
        try {
            node = switch (nodeFormat) {
                case InputNodeInter.NODE_FORMAT -> new InputNode(data.get(1).trim(), Integer.parseInt(data.get(2).trim()), Double.parseDouble(data.get(3).trim()), Boolean.parseBoolean(data.get(4).trim()));
                case ProcessNodeInter.NODE_FORMAT -> new ProcessNode(data.get(1).trim(), Integer.parseInt(data.get(2).trim()), Double.parseDouble(data.get(3).trim()));
                case OutputNodeInter.NODE_FORMAT -> new OutputNode(data.get(1).trim(), Integer.parseInt(data.get(2).trim()), Double.parseDouble(data.get(3).trim()));
                default -> throw new InValidNodeFormatException("정의되지 않은 NODE_FORMAT 입력. 값: " + nodeFormat);
            };
        } catch(InValidNodeFormatException ne){
            System.out.println("에러 메시지: " + ne.getMessage());
            ne.printStackTrace();
        }
        return node;
    }


    private HashMap<Integer, HashSet<Edge>> readEdgeFromFile(String inputFile){  // 파일로부터 엣지 정보를 읽어 시작노드 시리얼과 엣지를 묶어 저장.
        HashMap<Integer, HashSet<Edge>> result = new HashMap<>();
        String fileLine = "";
        ArrayList<String> splitInputData = null;
        try {
            FileReader fileInput = new FileReader(inputFile);
            BufferedReader inputBuffer = new BufferedReader(fileInput);
            for (int i = 1; (fileLine = inputBuffer.readLine()) != null; i++){  /// 파일에서 라인 읽어오기
                if(fileLine.trim().startsWith("$$"))    //라인의 가장 앞에 나오는 $$는 주석역할
                    continue;
                fileLine = fileLine.replace("[", "");
                splitInputData = new ArrayList<String>(Arrays.stream(fileLine.split("]")).toList());
                if(!result.containsKey(Integer.valueOf(splitInputData.get(1)))){
                    HashSet<Edge> tempHashSet = new HashSet<Edge>();
                    result.put(Integer.valueOf(splitInputData.get(1)), tempHashSet);
                }
                result.get(Integer.valueOf(splitInputData.get(1))).add(makeEdgeFromAList(splitInputData));
            }
        }catch (IOException ie){
            ie.printStackTrace();
        }
        return result;
    }


    private Edge makeEdgeFromAList(ArrayList<String> data){
        Edge edge = null;
        Integer startNodeSerial;
        int edgeFormat = Integer.parseInt(data.get(0).trim());
        try {
            if(edgeFormat == 1) {
                if (data.size() == 5)
                    edge = new Edge(Integer.parseInt(data.get(1)), Integer.parseInt(data.get(2)), Double.parseDouble(data.get(3)), Double.parseDouble(data.get(4)));
                else if (data.size() == 4)
                    edge = new Edge(Integer.parseInt(data.get(1)), Integer.parseInt(data.get(2)), Double.parseDouble(data.get(3)));
                else
                    throw new InValidEdgeFormatException("정의되지 않은 Edge 생성자 매개변수 개수 값: " + data.size());
            }else if(edgeFormat == 2){
                if(data.size() == 6)
                    edge = new LoopEdge(Integer.parseInt(data.get(1)), Integer.parseInt(data.get(2)), Double.parseDouble(data.get(3)), Double.parseDouble(data.get(4)), Integer.parseInt(data.get(5)));
                else if(data.size() == 5)
                    edge = new LoopEdge(Integer.parseInt(data.get(1)), Integer.parseInt(data.get(2)), Double.parseDouble(data.get(3)), Integer.parseInt(data.get(4)));
                else throw new InValidEdgeFormatException("정의되지 않은 LoopEdge 생성자 매개변수 개수 값: " + data.size());
            }
            else throw new InValidEdgeFormatException("정의되지 않은 EDGE_FORMAT 입력. 값: " + edgeFormat);
        } catch(InValidEdgeFormatException ee){
            System.out.println("에러 메시지: " + ee.getMessage());
            ee.printStackTrace();
        }
        return edge;
    }


    private HashMap<LevelData, HashMap<Node, HashSet<Edge>>> Matcher(HashMap<LevelData, HashSet<Node>> nodeMap, HashMap<Integer, HashSet<Edge>> edgeMap){
        HashMap<LevelData, HashMap<Node, HashSet<Edge>>> result = new HashMap<>();
        HashMap<Node, HashSet<Edge>> NodeNEdgeMap = new HashMap<>();
        HashSet<Node> tempNodeSet = new HashSet<>();
        for(LevelData ld: nodeMap.keySet()){
            result.put(ld, new HashMap<Node, HashSet<Edge>>());
        }
        for(HashSet<Node> i :nodeMap.values()) {
            tempNodeSet.addAll(i);
        }
        for(Integer i : edgeMap.keySet()){
            for(Node n : tempNodeSet){
                if(n.SERIAL_NUMBER == i){
                    NodeNEdgeMap.put(n, edgeMap.get(i));
                    edgeMap.remove(i);
                    tempNodeSet.remove(n);
                }
            }
        }
        for(LevelData ld: nodeMap.keySet()){
            HashSet<Node> ns = nodeMap.get(ld);
            for(Node n : NodeNEdgeMap.keySet()){
                if(ns.contains(n)){
                    result.get(ld).put(n, NodeNEdgeMap.get(n));
                }
            }
        }



        return result;
    }





    /*public static void main(String[] args) {
        Entity n = new Entity();
        HashMap<LevelData, HashSet> result = new HashMap<>();
        LevelData levelData = null;
        HashSet<Node> nodeData = null;
        String fileLine = "";
        ArrayList<String> splitInputData = null;
        try {

            FileReader fileInput = new FileReader("testData.txt");
            BufferedReader inputBuffer = new BufferedReader(fileInput);
            for (int i = 1; (fileLine = inputBuffer.readLine()) != null; i++){  /// 파일에서 라인 읽어오기
                if(fileLine.trim().startsWith("$$"))    //라인의 가장 앞에 나오는 $$는 주석역할
                    continue;
                if(fileLine.trim().startsWith("##")) {    //라인의 가장 첫번째에 오는 ## 새로운 레벨에 속하는 노드의 입력임을 나타냄.
                    fileLine = fileLine.replace("#", "");
                    fileLine = fileLine.replace("[", "");
                    splitInputData = new ArrayList<String>(Arrays.stream(fileLine.split("]")).toList());
                    levelData = n.makeLevelDataFromAList(splitInputData);
                    nodeData = new HashSet<Node>();
                    result.put(levelData, nodeData);
                    continue;
                }
                fileLine = fileLine.replace("[", "");
                splitInputData = new ArrayList<String>(Arrays.stream(fileLine.split("]")).toList());
                nodeData.add(n.makeNodeFromAList(splitInputData));
            }

        } catch (IOException ie){
            ie.printStackTrace();
        }
        System.out.println(result);
    }*/

}
























/*-------------------------------2.*개체에 관련된 데이터 저장소-------------------------------*/

/*-------------------------------2.1.LevelData-------------------------------*/

class LevelData{
    private final int NODE_FORMAT;
    private final int LEVEL;
    private final String LEVEl_NAME;

    LevelData(int nodeFormat, int level, String name) {

        this.NODE_FORMAT = nodeFormat;
        this.LEVEL = level;
        this.LEVEl_NAME = name;

    }

    @Override
    public int hashCode() {
        return (int) (NODE_FORMAT * 1000 + LEVEL);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof LevelData) {
            LevelData that = (LevelData) obj;
            if (this.hashCode() == that.hashCode())
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "" + this.hashCode();
    }

}


/*-------------------------------3.* 개체의 동작에 필요한 클래스-------------------------------*/

/*-------------------------------3.1.파일 입출력 관련 클래스-------------------------------*/
//첫 줄엔 레벨의 이름, 노드의 형식(input: 1, output: 2, process: 3) 두번째 줄 부터 각 노드 정보
//읽을 정보가 없는 줄의 첫 문자는 $ ($ 뒤에 나오는 정보들은 무시)
class IOHelper{
    String inputForm1 = "[level-name] [Node_FORMAT]";
    String inputForm2 = "[node-name] [critical] [";/// 작업 중단점 레벨없이 역할로만 구분되는 회로 구상
}