package com.etek.controller.utils;


import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.DetonatorEntity;
import com.etek.controller.persistence.gen.DetonatorEntityDao;
import java.util.List;

/**
 * 模拟请求的json串
 */
public class JsonUtils {
    /**
     * 模拟雷管数据
     * CheckoutDetailActivity2【91】
     */
    public static void monitDetonatorEntity(long proId) {
        List<DetonatorEntity> list = DBManager.getInstance().getDetonatorEntityDao().queryBuilder().where(DetonatorEntityDao.Properties.ProjectInfoId.eq(proId)).list();
        if (list != null && list.size() == 0){
            for (int i = 0; i < 8; i++) {
                DetonatorEntity detonatorEntity = new DetonatorEntity();
                switch (i) {
                    case 0:
                        detonatorEntity.setCode("6170725D02064");
                        detonatorEntity.setUid("61000001028324");
                        break;

                    case 1:
                        detonatorEntity.setCode("6170725D02007");
                        detonatorEntity.setUid("61000000916433");
                        break;

                    case 2:
                        detonatorEntity.setCode("6170725D02021");
                        detonatorEntity.setUid("61000000942128");
                        break;

                    case 3:
                        detonatorEntity.setCode("6170828D00650");
                        detonatorEntity.setUid("61000000651187");
                        break;

                    case 4:
                        detonatorEntity.setCode("6170725D02173");
                        detonatorEntity.setUid("61000000917033");
                        break;

                    case 5:
                        detonatorEntity.setCode("6170828D00632");
                        detonatorEntity.setUid("61000000909938");
                        break;

                    case 6:
                        detonatorEntity.setCode("6170828D00653");
                        detonatorEntity.setUid("61000000491245");
                        break;

                    case 7:
                        detonatorEntity.setCode("6170725D01651");
                        detonatorEntity.setUid("61000000920802");
                        break;
                }
                detonatorEntity.setProjectInfoId(proId);
                detonatorEntity.setRelay("100" + i);
                detonatorEntity.setHolePosition("孔" + i);
                DBManager.getInstance().getDetonatorEntityDao().insert(detonatorEntity);
            }
        }
    }

    /**
     * 模拟授权下载的数据
     */
    public static String getData(){
        return "{\"xmbh\":\"370100X15040023\",\"xmmc\":\"燃一测试项目\",\"dwdm\":\"3701004200003\",\"dwmc\":\"济南黄河爆破工程有限责任公司\",\"htbh\":\"370101318060002\",\"htmc\":\"燃一测试合同\",\"mmwj\":\"5ZOTl6XiSuRZKeIHm1U/nXo0w6mPVIVrCbZIMaZqwrwUmq0xEmS7KzSIW/NQRJqaQBdrWLxjmCHG4EoDDfNkrpKbgShTkX/Cq2ZgD022yZ7Dbug4wIoLmpOIZbO84I3V9bnQ+A4nkFEjrc8B3afuYodqGB7hyhDbdpjRDcGkojKH9BVa0oXvVFXhakcL8945WfNP3e0Z8d/A+GCFeZj/QipprsDVIlaUBaK0z6aKFlhSlxiNx+LI0rNxXCJNPIUWyOxVxGUcCGu9XiCJhk897d2PgLMz26eQNLheVFOsW0Qkd2FkyIWjw+XMFKrVgWedqvoUwsZn3Ou/pMVVtQ75bArL5CEnHCUuOs/mh3zTBnwZRCCjPjKkj2ZDJVJAKUOwoTf8g2/8TsV/jHUxJjAQWAIW2MgC8NbWPj7wjDONPyb+iJjKfYp3oOflp3pzUhWLgvvJ4YSWvFZYhkdO52otbYGUJmdMpoAU9bnQ+A4nkFFOv7fI2sLCX4dqGB7hyhDbvN8MY0i/2jOH9BVa0oXvVFXhakcL8945vqvwXL/5yQLA+GCFeZj/QjVKYp0MSced6+Mk1GPj4OKaUIWKmD60QBjHFLbua0Q/NoXue76xIhwmv6VBlXoSBhKGCP+oJd/balbQp+XbNZHSp4MHKOGQnvMi/QMJ6wskbKUpxk7pOlzLTJJYfNGMh7sHYakqtkTpqjkS7LYANPYFMqiF4ZG0ZWDJk73L1W8L+fR9mhIjjgiGJh+K+wVjqJyyos8IQZGrlKyAGSEzH6iXLJ/kJ1SLhk6YhOG8+ZgYfnmDAVCsz0yybrdsUpuv88MS63gu6f737W2uQ5wuBygckwg3ER8KOHyNRBIetwolyTSaf3QvMHKq+hTCxmfc69yptuu56P8Pv6TFVbUO+WwKy+QhJxwlLjrP5od80wZ8GUQgoz4ypI/5AoVkx3GODaE3/INv/E7FcTRSn60P1ewuay2jzus+gz4+8IwzjT8m/oiYyn2Kd6Dn5ad6c1IVi4L7yeGElrxWN8pZCBYf6jzhIDhVb9tmBvW50PgOJ5BRTr+3yNrCwl+Hahge4coQ2wmR9QeI2rSWeGow0EnmpA7hdwb5MBIiM5kxEhrwa0W4wPhghXmY/0I1SmKdDEnHnevjJNRj4+DimlCFipg+tECwM3EVCTdKCsQoR5VGJWreJr+lQZV6EgYShgj/qCXf22pW0Kfl2zWRp8LPAIjJeGXRuF0TCANLvGylKcZO6Tpcj0AZK8poscZ+UdMLC4bh9ao5Euy2ADT2BTKoheGRtGVgyZO9y9VvC8WNlD5Ig7c5nabgIR5r6jxzwhapUrAxIkoCu4g0xj8uY44zMQKflbv8mJ3uQ7ZlZzNg9PMu9nuKOPQl0JzyNNxF0QWwukYtt7NzXRvkUpJvteoo3CnNDzUzYPTzLvZ7ijhwBDHoXENNecaE7VUC4ca9nOoS2uBoksBgm9IsgqJmC3oPjVzX8eSPAa+2RGszzOYnli8a+UY/xiuI4BK5K8djBUz7JyGqm0N5stBp2toOM2D08y72e4qe2eH2x9hhX/kA3Uuo5H3HDjY3PK4gG8+6AYCWX9+T+0XRBbC6Ri23HQxXlq83O/nqGQ9ixNjU9gt6D41c1/HkFSC6z/dgYZGEWQkP8jqKAYJffQktwGH9c4/92u42UyfaKhYH1ZlcSBAD3E/KuowkT9vh2avCsduciaqmtBLDJbegpfHO4iwavdQsqEBbQ7z5O72ID6z7HLmlNozqTP4gjnkYWuJW1huvF8WAZbAUCZPP4Ed94GoXByE2wGjrsITdhoEXN18zkKQszZOt43E4Aub2bObiNFbTWBmX+bH/Pwci6rUshaDrJIsH6sWehYmbtpccoMjyyXP/lnF6iplupsKjGxe87O8KrqvRSQmuGle6mVIn/+H8\",\"result\":\"0\"}";
    }
}
