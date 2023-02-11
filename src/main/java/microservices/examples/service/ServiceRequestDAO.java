package microservices.examples.service;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.domain.Pageable;

@Mapper
public interface ServiceRequestDAO {

	@Select("<script>"
			+ "select\n"
			+ "	tsr.id,\n"
			+ "	tsr.title,\n"
			+ "	tsr.customer_id customerId,\n"
			+ "	tc.name customerName,\n"
			+ "	tsr.\"type\",\n"
			+ "	(select value from tb_code tco where tco.code = tsr.\"type\" and tco.code_type='SR_TYPE') typeName,\n"
			+ "	tsr.detail,\n"
			+ "	tsr.status,\n"
			+ "	(select value from tb_code tco where tco.code = tsr.status and tco.code_type='SR_STATUS') statusName,\n"
			+ "	tsr.call_agent_id callAgentId,\n"
			+ "	tuc.\"name\" callAgentName,\n"
			+ "	tsr.voc_assgnee_id vocAssgneeId,\n"
			+ "	tua.\"name\" vocAssgneeName,\n"
			+ "	tsr.voc_assgnee_dept_id vocAssgneeDeptId,\n"
			+ "	td.\"name\" vocAssgneeDeptName,\n"
			+ "	tsr.created,\n"
			+ "	tsr.updated\n"
			+ "from\n"
			+ "	tb_service_request tsr\n"
			+ "left join tb_customer tc on\n"
			+ "	tsr.customer_id = tc.id\n"
			+ "left join tb_dept td on\n"
			+ "	tsr.voc_assgnee_dept_id = td.id\n"
			+ "left join tb_user tua on\n"
			+ "	tsr.voc_assgnee_id = tua.id\n"
			+ "left join tb_user tuc on\n"
			+ "	tsr.call_agent_id = tuc.id\n"
			+ "<if test='type != null'>"
			+ "where tsr.type = #{type}"
			+ "</if>"
			+ "	order by \n"
            + " <foreach collection='pageable.sort' item='order' index='index' open='' separator=',' close=''>\n"
            + "    ${order.property} ${order.direction}\n"
            + " </foreach>\n"
			+ "	limit #{pageable.pageSize} offset #{pageable.offset} \n"
			+ ";\n"
			+ "</script>")
	public List<ServiceRequest> selectAllWithJoin(String type, @Param("pageable")Pageable pageable);

	@Select("<script>"
			+ "select\n"
			+ "	tsr.id,\n"
			+ "	tsr.title,\n"
			+ "	tsr.customer_id customerId,\n"
			+ "	tsr.\"type\",\n"
			+ "	tsr.detail,\n"
			+ "	tsr.status,\n"
			+ "	tsr.call_agent_id callAgentId,\n"
			+ "	tsr.voc_assgnee_id vocAssgneeId,\n"
			+ "	tsr.voc_assgnee_dept_id vocAssgneeDeptId,\n"
			+ "	tsr.created,\n"
			+ "	tsr.updated\n"
			+ "from\n"
			+ "	tb_service_request tsr\n"
			+ "where 1=1"
			+ "<if test='type != null'>"
			+ "		and tsr.type = #{type}"
			+ "</if>"
			+ "<if test='customerId != null'>"
			+ "		and tsr.customer_id = #{customerId}"
			+ "</if>"
			+ "<if test='pageable.sort.sorted'>"
			+ "	 <trim prefix='order by'> \n"
            + "     <foreach collection='pageable.sort' item='order' index='index' open='' separator=',' close=''>\n"
            + "        ${order.property} ${order.direction}\n"
            + "     </foreach>\n"
            + "  </trim>"
			+ "</if>"
			+ "	limit #{pageable.pageSize} offset #{pageable.offset} \n"
			+ ";\n"
			+ "</script>")
	public List<ServiceRequest> selectAll(String type, String customerId, @Param("pageable")Pageable pageable);

	public default List<ServiceRequest> selectAll(String type, @Param("pageable")Pageable pageable) {
		return selectAll(type, null, pageable);
	}
	public default List<ServiceRequest> selectAll(@Param("pageable")Pageable pageable) {
		return selectAll(null, null, pageable);
	}

	@Select("<script>"
			+ "select count(1)\n"
			+ "from\n"
			+ "	tb_service_request tsr\n"
			+ ";\n"
			+ "</script>")
	public long countAll();
	
}
