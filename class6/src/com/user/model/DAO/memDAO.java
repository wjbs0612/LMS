package com.user.model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import util.DB;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.user.model.DTO.memDTO;





import util.DB;

public class memDAO {//mypage DAO
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;
	
		//mypage에 표시할 내용 기능
		public List<memDTO> getMyPage(String memId) throws SQLException {
		
		List<memDTO> list = new ArrayList<memDTO>();
		String sql1="SELECT LECNAME,LECSTART,LECEND FROM LECHURE WHERE LECNO=(SELECT LECNO FROM PRIVACY WHERE MEMID=?)";
		String sql2="SELECT GRAJAVA,GRAWEB,GRADB FROM GRADE WHERE MEMID=?";
		String sql3="SELECT COUNT(*) AS CNT FROM CHK WHERE MEMID=? AND (TO_CHAR( CHKIPD, 'YYYY-MM-DD' ) > SYSDATE-1)";
		
		System.out.println(memId);//id 확인출력
		memDTO mDTO = new memDTO();
		try{
			try{
				conn=DB.getConnction();
				//conn=db.getConnction();
			}catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//강의에 대한 내용
			pstmt=conn.prepareStatement(sql1);
			pstmt.setString(1, memId);
			rs=pstmt.executeQuery();
			if(rs.next()){
				mDTO.setLecName(rs.getString("lecName"));
				mDTO.setLecStart(rs.getDate("lecStart"));
				mDTO.setLecEnd(rs.getDate("lecEnd"));
			}
			//성적에 대한 내용
			pstmt=conn.prepareStatement(sql2);
			pstmt.setString(1, memId);
			rs=pstmt.executeQuery();
			if(rs.next()){
				mDTO.setGraJava(rs.getInt("graJava"));
				mDTO.setGraWeb(rs.getInt("graWeb"));
				mDTO.setGraDb(rs.getInt("graDb"));
			}
			//출석에 대한 내용
			pstmt=conn.prepareStatement(sql3);
			pstmt.setString(1, memId);
			rs=pstmt.executeQuery();
			String result;
			if(rs.next()){
				if(rs.getInt("cnt")>=1){result="출석완료";}else{result="미출석";};
				mDTO.setChk(result);
			}
			
			list.add(mDTO);
			//conn.commit();
		}catch(SQLException e){
			//conn.rollback();
		}finally{
			conn.setAutoCommit(true);
			if(rs!=null)rs.close();
			if(pstmt!=null)pstmt.close();
			if(conn!=null)conn.close();
		}
		
		System.out.println("완료");
		return list;
		}

		// 회원 정보 조회
	    public List<memDTO> getUserInfo(String memId) throws SQLException {
	    	String sql="SELECT * FROM PRIVACY WHERE MEMID=?";
	    	
	    	List<memDTO> list = new ArrayList<memDTO>();
	    	memDTO mDTO = new memDTO();
			try {
				try{
					conn=DB.getConnction();
					//conn=db.getConnction();
				}catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				//출석에 대한 내용
				pstmt=conn.prepareStatement(sql);
				pstmt.setString(1, memId);
				rs=pstmt.executeQuery();
				if(rs.next()){
					mDTO.setMemId(rs.getString("memId"));
					mDTO.setMemName(rs.getString("memName"));
					mDTO.setMemGen(rs.getString("memGen"));
					mDTO.setMemBirth(rs.getDate("memBirth"));
					mDTO.setMemMail(rs.getString("memMail"));
					mDTO.setMemPnum(rs.getInt("memPnum"));
				}
				
				list.add(mDTO);
				//conn.commit();
			}catch(SQLException e){
				//conn.rollback();
			}finally{
				conn.setAutoCommit(true);
				if(rs!=null)rs.close();
				if(pstmt!=null)pstmt.close();
				if(conn!=null)conn.close();
			}
			
			System.out.println("완료");
			return list;
			}
	    }// end getUserInfo
	    
	    /*// 회원 정보 수정
		public void updateMem(memDTO member) throws SQLException {

			try {

				sb1 = new StringBuffer();
				sb1.append("UPDATE MEMBER SET MEMPW=?, MEMMAIL=?, MEMPNUM=? WHERE MEMID=?");

				conn = DB.getConnction();
				pstmt = conn.prepareStatement(sb1.toString());

				// 자동 커밋을 false로
				conn.setAutoCommit(false);
				
				pstmt.setString(1, member.getMemPw());
				pstmt.setString(2, member.getMemMail());
				pstmt.setInt(3, member.getMemPnum());
				pstmt.setString(4, member.getMemId());

				pstmt.executeUpdate();
				// 완료시 커밋
				conn.commit(); 
							
			} catch (Exception e) {
				conn.rollback(); // 오류시 롤백
				throw new RuntimeException(e.getMessage());
			} finally {
				try{
					if ( pstmt != null ){ pstmt.close(); pstmt=null; }
					if ( conn != null ){ conn.close(); conn=null; }
				}catch(Exception e){
					throw new RuntimeException(e.getMessage());
				}
			}		
		}// end updateMem
		
		// 회원 정보 삭제	 
		public int delMem(String id, String pw) {

			String dbpw = ""; // DB상의 비밀번호를 담아둘 변수
			int x = -1;

			try {
				// 비밀번호 조회
				sb1 = new StringBuffer();
				sb1.append("SELECT MEMPW FROM MEMBER WHERE MEMID=?");

				// 회원 삭제
				sb2 = new StringBuffer();
				sb2.append("DELETE FROM MEMBER WHERE MEMID=?");

				conn = DB.getConnction();

				// 자동 커밋을 false로 한다.
				conn.setAutoCommit(false);
				
				// 1. 아이디에 해당하는 비밀번호를 조회한다.
				pstmt = conn.prepareStatement(sb1.toString());
				pstmt.setString(1, id);
				rs = pstmt.executeQuery();

				if (rs.next()) 
				{
					dbpw = rs.getString("memPw");
					if (dbpw.equals(pw)) // 입력된 비밀번호와 DB비번 비교
					{
						// 같을경우 회원삭제 진행
						pstmt = conn.prepareStatement(sb2.toString());
						pstmt.setString(1, id);
						pstmt.executeUpdate();
						conn.commit(); 
						x = 1; // 삭제 성공
					} else {
						x = 0; // 비밀번호 다름 - 삭제 실패
					}
				}

				return x;

			} catch (Exception sqle) {
				try {
					conn.rollback(); // 오류시 롤백
				} catch (SQLException e) {
					e.printStackTrace();
				}
				throw new RuntimeException(sqle.getMessage());
			} finally {
				try{
					if ( pstmt != null ){ pstmt.close(); pstmt=null; }
					if ( conn != null ){ conn.close(); conn=null; }
				}catch(Exception e){
					throw new RuntimeException(e.getMessage());
				}
			}
		}// end delMem
}
*/