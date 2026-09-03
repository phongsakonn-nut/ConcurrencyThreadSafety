/**
 * การโอนเงินระหว่างสองบัญชี — ไฟล์ที่นิสิตต้องแก้ (ส่วนที่ 2)
 *
 * การโอนต้องล็อกสองใบพร้อมกัน เพราะระหว่างที่หักจากบัญชีต้นทาง
 * แล้วยังไม่ทันเพิ่มให้ปลายทาง เงินจะ "หายไปจากระบบ" ชั่วขณะ
 * ถ้ามีใครมาอ่านยอดรวมตอนนั้นจะได้ตัวเลขที่ผิด
 *
 * ปัญหาคือ การถือล็อกสองใบพร้อมกัน คือสูตรสำเร็จของ deadlock
 */
public class Bank {

    private Bank() {
        // utility class — ห้ามสร้าง object
    }

    /**
     * โอนเงินจากบัญชีหนึ่งไปอีกบัญชีหนึ่ง
     *
     * @return true ถ้าโอนสำเร็จ, false ถ้าเงินต้นทางไม่พอ
     * @throws IllegalArgumentException ถ้า argument ไม่ถูกต้อง หรือโอนเข้าบัญชีตัวเอง
     */
    public static boolean transfer(Account from, Account to, int amount) { //เติม synchronized ได้ แต่ไม่ควร
        if (from == null || to == null) {
            throw new IllegalArgumentException("accounts must not be null");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        if (from == to) {
            throw new IllegalArgumentException("cannot transfer to the same account");
        }

        // ---------------------------------------------------------------
        // TODO 2  ลำดับการล็อก
        //
        // ตอนนี้ลำดับล็อกขึ้นกับว่าใครโอนให้ใคร:
        //     transfer(A, B, ...) จะล็อก A ก่อน แล้วค่อย B
        //     transfer(B, A, ...) จะล็อก B ก่อน แล้วค่อย A
        //
        // ถ้าสองอย่างนี้เกิดพร้อมกัน ต่างฝ่ายต่างถือสิ่งที่อีกฝ่ายรอ
        // ไม่มี error ไม่มี exception โปรแกรมแค่ค้างเงียบ ๆ
        //
        // งานของคุณ: ทำให้ทุกเธรดขอล็อกใน "ลำดับเดียวกันเสมอ"
        //            ไม่ว่าจะโอนไปทางไหน โดยใช้ from.id() และ to.id()
        //
        // ห้ามแก้ด้วยการเอาล็อกใบใดใบหนึ่งออก — ยอดรวมจะเพี้ยน
        // ---------------------------------------------------------------
        
        /* ที่อาจารย์เขียนให้ดูเป้นตัวอย่าง
           Account first = from ;
           Account second = to ;
           if(from.id() < to.id()){
           first = from;
           second = to; } */
           
        Account first = (from.id() < to.id()) ? from : to; //ลำดับแรกที่ล็อก เพราะถ้าโอนจาก A ไป B จะล็อก A ก่อน แล้วค่อย B แต่ถ้าโอนจาก B ไป A จะล็อก B ก่อน แล้วค่อย A
            Account second = (first == from) ? to : from; //ลำดับสองที่ล็อก เพราะถ้าโอนจาก A ไป B จะล็อก A ก่อน แล้วค่อย B แต่ถ้าโอนจาก B ไป A จะล็อก B ก่อน แล้วค่อย A
            synchronized (first){//ล็อกอันแรกก่อน
                synchronized (second){//ล็อกอันที่สอง 
                    if (from.withdraw(amount)) {
                        to.deposit(amount);
                        return true;
                    } else {
                        return false;
                    }
                }
            }     
    }
}
