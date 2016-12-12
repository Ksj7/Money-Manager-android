package com.tonight.manage.organization.managingmoneyapp.Builder;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.tonight.manage.organization.managingmoneyapp.MyApplication;
import com.tonight.manage.organization.managingmoneyapp.Object.EventInfoMemberPaymentListItem;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * Created by sujinKim on 2016-12-01.
 */

public class ProductExcel implements Serializable {

    File excel;
    ArrayList<EventInfoMemberPaymentListItem> memberPaymentList;
    String groupName;

    public static class ExcelBuilder {
        private String group;
        private ArrayList<EventInfoMemberPaymentListItem> member;

        public ExcelBuilder(String groupName, ArrayList<EventInfoMemberPaymentListItem> memberList) {
            this.group = groupName;
            this.member = memberList;
        }

        public ProductExcel build() throws IOException, WriteException {
            return new ProductExcel(this);
        }

    }

    public ProductExcel(ExcelBuilder builder) throws IOException, WriteException {

        this.groupName = builder.group;
        this.memberPaymentList = builder.member;

        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AMM";
        File dir = new File(dirPath);
        boolean isDirectoryCreated = dir.exists();
        if (!isDirectoryCreated) {
            isDirectoryCreated = dir.mkdir();
        }
        if (!isDirectoryCreated) {
            Toast.makeText(MyApplication.getItemContext(), "폴더를 생성할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        excel = new File(dirPath + "/" + groupName + ".xls");

        boolean isExcelCreated = excel.exists();

        if (isExcelCreated) {
            if (excel.delete()) {
                Log.i("excel", "기존 파일 file remove = " + excel.getName() + ", 삭제 성공");
            } else {
                Log.i("excel", "기존 파일 file remove = " + excel.getName() + ", 삭제 실패");
            }
        }

        isExcelCreated = excel.createNewFile();

        if (!isExcelCreated) {
            Toast.makeText(MyApplication.getItemContext(), "엑셀파일을 생성할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        WritableWorkbook workbook = Workbook.createWorkbook(excel);
        WritableSheet sheet = workbook.createSheet(groupName, 0);

        Label name;
        Label paymentStatus;

        sheet.addCell(new Label(0, 0, "이름"));
        sheet.addCell(new Label(1, 0, "납부 여부"));

        for (int i = 0; i < memberPaymentList.size(); i++) {
            EventInfoMemberPaymentListItem member = this.memberPaymentList.get(i);
            name = new Label(0, i + 1, member.getName());
            paymentStatus = new Label(1, i + 1, member.getSpendingstatus() + "");
            sheet.addCell(name);
            sheet.addCell(paymentStatus);
        }

        workbook.write();
        workbook.close();
    }
}
