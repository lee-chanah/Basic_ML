
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

import jdk.jshell.spi.ExecutionControl;

import java.util.*;
/*-------------------------------1.개체의 기본형-------------------------------*/

public class Entity {
    private HashMap<LevelData, HashMap> mindCircuit = new HashMap<>();
    private HashSet<Node> previousSparkedNode = new HashSet<>();
    private HashSet<Node> currentSparkedNode = new HashSet<>();

    Entity(ArrayList<String> inputFileList, ArrayList<String> outputFileList){

    }

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
        int result = (int) (NODE_FORMAT * 1000 + LEVEL);
        return result;
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