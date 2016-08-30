package com.i3cnam.gofast.model;

import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import com.i3cnam.gofast.dao.CarpoolDAO;
import com.i3cnam.gofast.dao.CourseDAO;
import com.i3cnam.gofast.dao.PlaceDAO;
import com.i3cnam.gofast.dao.TravelDAO;
import com.i3cnam.gofast.dao.UserDAO;
import com.i3cnam.gofast.server.Management;

public class Test {

	public static void main(String[] args) {

		/*
		UserDAO uDao = new UserDAO();
		User u;
		u = uDao.get("titi42");
		
		if (u == null) {
			u = new User();
			u.setNickname("titi42");
			
			uDao.create(u);
		}
		
		DriverCourse c = new DriverCourse();
		c.setOrigin(new Place("a","b"));
		c.getOrigin().setCoordinates(new LatLng(43.6098191,1.4399979));
		
		c.setDestination(new Place("c","d"));
		c.getDestination().setCoordinates(new LatLng(44.0221821,1.3528962));
		
		c.setEncodedPoints("kpdiG_gxG?pC?TZ?d@BR^tB~D~@fBI\\IPUV{@r@aC`AoDdBYd@OJVlAF`@Z`B|B`K~DhRpC|LXhA@~@cMte@e@rB_@J{AMy@JWFCd@R`DH|ACdASj@i@^W?e@A_AYwKyAu@DaGm@eMgAeCEoBP_B`@qCjA_DdAeC^}AFkDMeCe@qCiAsCkBgFmDaCs@yBYkI]ap@sEsUyAoVPcGDmKA_DUoB_@wBu@oC_BmFeFqBcC}AaCo@sAq@cCU}BAgCKmDe@{Ck@sAu@y@oAe@_AC_AXeC|B{AfAqD|AkFlCOVc@bBe@n@wBzEuA|BsBxAyPjHgPlH}I~EsK|GgF`DsJdFgF`CaKxDmGjBgIpBow@nO_P|DsBl@}R|GoIfCqEhAmMnCyIvBaQ~EsEjAkLfCmt@~NoLdCa|@lQiM`DaNpEcNjGcPnJaZ~QyIvE{DhBiGbCkEvAqIbCiKvBaJpAwGh@sCRuIZuKH_TEqJ@oABqNn@iGj@{NjBkBVkY~CqQz@qRX}M\\gGf@uDl@wFtAoGvBoFdCoGtDkF`E_HzGmHhJgLdOoF`GqE`EuEjDaF`DsRjK{ItFmJtHuCpCsHpIaI|KgMvTyIlOuHbLwFbHuG~GaKrIeNhJ_U|NqJ`H}HxGiJhJwJjLkLtPaEvGsQ|YcFfHsGtHmHzGuFdEsFjDuHtD_GzBgD`AqEfAeHjAeFf@wDPyJFkY}@sKS{IBuJ\\sMlAgVpC{LjA}Ll@}IDaGGcLa@yFUoVy@yPLmKj@c]`DwFN}ECaKu@aF{@qEgAuOyEmG}AcDg@aFi@sCMqJCyEVkEf@{GjAsInCiJpEuE|CoCtB{BnB{ItJ_CrCC?a@b@{ClDg@Zw@Vw@F_B[mAiAk@sAQs@Yk@mAmYa@sIYuFMs@m@kAa@Qe@CSDeAj@}BnBw@XeANkAGsFaAyXaGsl@qMmf@mKsWaH_QeEm]qHaUsFw_@uIuRuDwFcAyTsD}TqDmJsAuFOgFJaEAyB_@gBq@}AcAiCwC{@{AgBuFeCcO{AmJc@eEcA_OIqBYwCF}C\\}EBiAJY@WKWMAQNUH_CPqDLuAEw@SS[WKYFSTG`ANd@Jp@K|@UPk@G{AyAwAoAiFNs`@dBqDHwFTuRdAgEPsAAuBg@kBMq@UOGOHCJADs@^{@?sA_@sDqAy@YcBSsE}@o@_@iAoB_@a@s@AuBXaIvAUIGEMFe@q@s@gBg@m@aA{@W_AC_ALaBzA_JJIHa@Q]YBeBTsD[{Gi@Z~JGrBCJFT");
		
		c.setDriver(u);
		
		CourseDAO cDao = new CourseDAO();
		int courseId = cDao.create(c);
		
		DriverCourse c2 = cDao.get(courseId);
		c2.defineBounds();
		System.out.println("INSERTED:");
		System.out.println(c2);

		List<DriverCourse> results;
		LatLng point1 = null;
		LatLng point2 = null;
		
		point1 = new LatLng(44.0 , 1.5);
		System.out.println("RESULTS FOR: " + point1 + point2);
		results =  cDao.findByZone(point1, point2);
		for (DriverCourse dc : results) {
			System.out.println(dc);
		}

		point1 = new LatLng(44.0 , 1.4);
		System.out.println("RESULTS FOR: " + point1 + point2);
		results =  cDao.findByZone(point1, point2);
		for (DriverCourse dc : results) {
			System.out.println(dc);
		}

		point1 = new LatLng(44.0 , 1.4);
		point2 = new LatLng(43.9 , 1.4);
		System.out.println("RESULTS FOR: " + point1 + point2);
		results =  cDao.findByZone(point1, point2);
		for (DriverCourse dc : results) {
			System.out.println(dc);
		}
		
		point1 = new LatLng(44.0 , 1.4);
		point2 = new LatLng(43.9 , 1.5);
		System.out.println("RESULTS FOR: " + point1 + point2);
		results =  cDao.findByZone(point1, point2);
		for (DriverCourse dc : results) {
			System.out.println(dc);
		}
		*/
		TravelDAO tDao = new TravelDAO();
		CarpoolDAO cDao = new CarpoolDAO();
		CourseDAO courseDao = new CourseDAO();

		System.out.println("FOR TRAVEL 2");		
		for (Carpooling c : cDao.getByTravel(tDao.get(2))) {
			System.out.println(c);
		}
		System.out.println("FOR COURSE 7");
		for (Carpooling c : cDao.getByCourse(courseDao.get(7),true)) {
			System.out.println(c);
		}
		
		System.out.println("NEW Match Find");
		Management.findMatches(2);
		for (Carpooling c : cDao.getByTravel(tDao.get(2))) {
			System.out.println(c);
		}
		
		
	}

}
