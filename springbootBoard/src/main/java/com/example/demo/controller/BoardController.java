package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.constant.Method;
import com.example.demo.domain.BoardDTO;
import com.example.demo.service.BoardService;
import com.example.demo.util.UiUtils;

@Controller
public class BoardController extends UiUtils {
	
	@Autowired
	private BoardService boardService;
	
	@GetMapping(value = "/board/write.do")
	public String openBoardWriter(@RequestParam(value = "idx", required = false) Long idx, Model model) {
		if(idx == null) {
			model.addAttribute("board", new BoardDTO());
		} else {
			BoardDTO board = boardService.getBoardDetail(idx);
			if(board == null) {
				return "redirect:/board/list.do";
			}
			model.addAttribute("board", board);
		}
	return "board/write";
	}
	
	@PostMapping(value = "/board/register.do")
	public String registerBoard(final BoardDTO params, Model model)  {
		try { 
			boolean isRegistered = boardService.registerBoard(params);
			if(isRegistered == false) {
				//게시글 등록에 실패했다는 메시지 출력 
				return showMessageWithRedirect("게시글 등록 실패", "board/list.do", Method.GET, null, model);
			}
		} catch(DataAccessException e){
			//디비 처리 과정에 문제가 발생했다는 메시지 출력 
			return showMessageWithRedirect("데이터베이스 처리 과정에 문제가 발생하였습니다.", "/board/list.do", Method.GET, null, model);
		} catch(Exception e){
			//시스템 문제가 발생했 메시지 출력
			return showMessageWithRedirect("시스템에 문제가 발생하였습니다.", "/board/list.do", Method.GET, null, model);
			}
		return showMessageWithRedirect("게시글 등록이 완료되었습니다.", "/board/list.do", Method.GET, null, model);
	}
	
	@GetMapping(value = "/board/list.do")
	//Model : 컨트롤러에서 view단으로 데이터를 전달하는 데 사용되는 인터페이
	public String openBoardList(Model model) {
		List<BoardDTO> boardList = boardService.getBoardList();
		model.addAttribute("boardList", boardList);
		return "board/list";
	}
	
	@GetMapping(value = "/board/view.do")
	public String openBoardDetail(@RequestParam(value="idx", required = false) Long idx, Model model) {
		if(idx == null) {
			//올바르 않은 접근이라는 메시지 전달 후, 게시글 리스트로 리다이렉트 
			return "redirect:/board/list.do";
		}
		
		BoardDTO board = boardService.getBoardDetail(idx);
		
		if(board == null || "Y".equals(board.getDeleteYn())) {
			return "redirect:/board/list.do";
		}
			//없는 게시글이거나 이미 삭제된 게시글이라는 메시지 전달 후, 게시글 리스트로 리다이렉트 
			model.addAttribute("board", board);
			return "board/view";
	}
	
	@PostMapping(value = "/board/delete.do")
	public String deleteBoard(@RequestParam(value = "idx", required = false) Long idx, Model model) {
		if(idx == null) {
			//올바르지 않은 접근이라 메시지 전달 후, 게시글 리트로 리다이렉트 
			return showMessageWithRedirect("올바르지 않은 접근입니다.", "/board/list.do", Method.GET, null, model);
		}
		
		try {
			boolean isDeleted = boardService.deleteBoard(idx);
			if(isDeleted==false) {
				//게시글 삭제에 실패했다는 메시지 전달 
				return showMessageWithRedirect("게시글 삭제에 실패하였습니다.", "/board/list.do", Method.GET, null, model);
			}
		}catch(DataAccessException e){
			//데이터베이스 처리 과정에 문제가 발생했다는 메시지 전달 
			return showMessageWithRedirect("데이터베이스 처리 과정에 문제가 발생하였습니다.", "/board/list.do", Method.GET, null, model);
		}catch (Exception e) {
			//시스템에 문제가 발생했다는 메시지 전달 
			return showMessageWithRedirect("시스템에 문제가 발생하였습니다.", "/board/list.do", Method.GET, null, model);
		}
		return showMessageWithRedirect("게시글 삭제가 완료되었습니다.", "/board/list.do", Method.GET, null, model);
	}
}
