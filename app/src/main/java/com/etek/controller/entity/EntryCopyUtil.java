package com.etek.controller.entity;

import com.etek.controller.persistence.entity.PendingProject;
import com.etek.controller.persistence.entity.ProjectDetonator;
import com.etek.sommerlibrary.utils.MD5Util;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

public class EntryCopyUtil {

    public static DetController copyInfoToDetController(DetController detController, PendingProject pendingProject) {
        if (detController == null && pendingProject == null) {
            return null;
        }

        detController.setCompany(pendingProject.getCompanyName());
        detController.setCompanyCode(pendingProject.getCompanyCode());
        detController.setSn(pendingProject.getControllerId());
        detController.setDetCount(pendingProject.getDetonatorList().size());
        detController.setProjectId(pendingProject.getId()+"");
        detController.setLatitude(pendingProject.getLatitude());
        detController.setLongitude(pendingProject.getLongitude());
        detController.setToken(getToken(pendingProject.getDetonatorList()));
        detController.setDetList(getDetList(pendingProject.getDetonatorList()));
        return detController;
    }

    private static List<Detonator> getDetList(List<ProjectDetonator> detonatorList) {
        ArrayList<Detonator> detonators= new ArrayList<>();
        for (ProjectDetonator projectDetonator : detonatorList) {
            Detonator detonator = new Detonator();
            detonator.setRelay(projectDetonator.getRelay());
            detonator.setStatus(projectDetonator.getStatus());
            detonator.setUid(projectDetonator.getUid());
            detonators.add(detonator);
        }
        return detonators;
    }

    public static String getToken(List<ProjectDetonator> detonatorList) {
        StringBuilder sb = new StringBuilder();
        for (ProjectDetonator detonator : detonatorList) {
            sb.append(detonator.getCode());
        }
//        XLog.i(JSON.toJSONString(detList));
        Logger.i("sb:" + sb.toString());
        String token = MD5Util.md5(sb.toString());;
        return token;
    }
}
