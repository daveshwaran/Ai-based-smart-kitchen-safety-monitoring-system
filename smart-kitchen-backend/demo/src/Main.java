import java.util.*;

class Main {
    public static void main(String[] args) {
        example2 ex = new example2();
        ex.print(2);
    }
}
class example{
    void print(int a){
        System.out.println(a);
    }
    void print(int a, int b){
        System.out.println(b);
    }
}
class example2 extends example {
    @override
    void print(int a) {
        System.out.println(a);
    }
}