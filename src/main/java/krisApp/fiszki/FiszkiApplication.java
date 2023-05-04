package krisApp.fiszki;

import java.io.File;
import java.io.IOException;
import java.util.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@SpringBootApplication
@Controller
public class FiszkiApplication {

	private String fileName;
	private int currentWordIndexFlash = 0;
	private int currentWordIndexQuiz = 0;
	private int currentTranslationIndex = 1;
	private List<String> words = new ArrayList<>();

	public static void main(String[] args) {
		SpringApplication.run(FiszkiApplication.class, args);
	}

	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("fileName", fileName);
		return "index";
	}

	@PostMapping("/upload")
	public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
		if (file.isEmpty()) {
			return "redirect:/";
		}
		fileName = StringUtils.cleanPath(file.getOriginalFilename());
		words.clear();
		File tempFile = File.createTempFile("temp", ".xlsx");
		file.transferTo(tempFile);
		Workbook workbook = WorkbookFactory.create(tempFile);
		Sheet sheet = workbook.getSheetAt(0);
		for (Row row : sheet) {
			Cell leftCell = row.getCell(0);
			Cell rightCell = row.getCell(1);
			if (leftCell != null && rightCell != null) {
				String leftCellValue = leftCell.getStringCellValue();
				String rightCellValue = rightCell.getStringCellValue();
				if (!StringUtils.isEmpty(leftCellValue) && !StringUtils.isEmpty(rightCellValue)) {
					words.add(leftCellValue + "|" + rightCellValue);
				}
			}
		}
		workbook.close();
		tempFile.delete();
		return "redirect:/";
	}

	@GetMapping("/flashCards")
	public String showFlashcards(Model model) {
		if (words.isEmpty()) {
			return "redirect:/";
		}
		String currentWord = words.get(currentWordIndexFlash).split("\\|")[0];
		String currentTranslation = words.get(currentWordIndexFlash).split("\\|")[1];
		model.addAttribute("currentWord", currentWord);
		model.addAttribute("currentTranslation", currentTranslation);
		model.addAttribute("currentWordIndex", currentWordIndexFlash);
		return "flashCards";
	}

	@GetMapping("/nextFlashcard")
	public String nextFlashcard(@RequestParam(required = false) Integer startIndex, Model model) {
		if (startIndex != null) {
			currentWordIndexFlash = startIndex;
			currentTranslationIndex = startIndex;
			String currentWord = words.get(currentWordIndexFlash).split("\\|")[0];
			String currentTranslation = words.get(currentWordIndexFlash).split("\\|")[1];
			model.addAttribute("currentWord", currentWord);
			model.addAttribute("currentTranslation", currentTranslation);
			model.addAttribute("currentWordIndex", currentWordIndexFlash);
			return "flashCards";
		}
		if (currentWordIndexFlash < words.size() - 1) {
			currentWordIndexFlash++;
			currentTranslationIndex++;
			String currentWord = words.get(currentWordIndexFlash).split("\\|")[0];
			String currentTranslation = words.get(currentWordIndexFlash).split("\\|")[1];
			model.addAttribute("currentWord", currentWord);
			model.addAttribute("currentTranslation", currentTranslation);
			model.addAttribute("currentWordIndex", currentWordIndexFlash);
		} else {
			model.addAttribute("currentWord", "Koniec");
		}
		return "flashCards";
	}



	@GetMapping("/list")
	public String showList(Model model) {
		if (words.isEmpty()) {
			return "redirect:/";
		}
		List<String> list = new ArrayList<>();
		for (String word : words) {
			String[] parts = word.split("\\|");
			String left = parts[0];
			String right = parts[1];
			String item = left + " - " + right;
			list.add(item);
		}
		model.addAttribute("list", list);
		return "list";
	}

	@GetMapping("/quiz")
	public String quiz(Model model) {

		if (words.isEmpty()) {
			return "redirect:/";
		}
		else {
			String checkWord = words.get(currentWordIndexQuiz+4).split("\\|")[0];
			String currentWord = words.get(currentWordIndexQuiz).split("\\|")[0];
			String currentAnswer = words.get(currentWordIndexQuiz).split("\\|")[1];

			if (checkWord != null) {
				String question1 = words.get(currentWordIndexQuiz + 1).split("\\|")[1];
				String question2 = words.get(currentWordIndexQuiz + 2).split("\\|")[1];
				String question3 = words.get(currentWordIndexQuiz + 3).split("\\|")[1];

				model.addAttribute("currentWord", currentWord);
				model.addAttribute("currentAnswer", currentAnswer);
				model.addAttribute("question1", question1);
				model.addAttribute("question2", question2);
				model.addAttribute("question3", question3);
				model.addAttribute("currentWordIndex", currentWordIndexQuiz);
				return "quiz";
			}

			else {
				String question1 = words.get(currentWordIndexQuiz - 1).split("\\|")[1];
				String question2 = words.get(currentWordIndexQuiz - 2).split("\\|")[1];
				String question3 = words.get(currentWordIndexQuiz - 3).split("\\|")[1];

				model.addAttribute("currentWord", currentWord);
				model.addAttribute("currentAnswer", currentAnswer);
				model.addAttribute("question1", question1);
				model.addAttribute("question2", question2);
				model.addAttribute("question3", question3);
				model.addAttribute("currentWordIndex", currentWordIndexQuiz);

				return "quiz";
			}
		}
	}

	@GetMapping("/nextQuiz")
	public String nextQuiz(@RequestParam(required = false) Integer startIndex, Model model) {
		if (startIndex != null) {
			currentWordIndexQuiz = startIndex;
			currentTranslationIndex = startIndex;
			String question1 = words.get(currentWordIndexQuiz + 1).split("\\|")[1];
			String question2 = words.get(currentWordIndexQuiz + 2).split("\\|")[1];
			String question3 = words.get(currentWordIndexQuiz + 3).split("\\|")[1];
			String currentWord = words.get(currentWordIndexQuiz).split("\\|")[0];
			String currentAnswer = words.get(currentWordIndexQuiz).split("\\|")[1];
			model.addAttribute("currentWord", currentWord);
			model.addAttribute("currentAnswer", currentAnswer);
			model.addAttribute("question1", question1);
			model.addAttribute("question2", question2);
			model.addAttribute("question3", question3);
			model.addAttribute("currentWordIndex", currentWordIndexQuiz);
			return "quiz";
		}
		if (currentWordIndexQuiz < words.size() - 1) {
			currentWordIndexQuiz++;
			currentTranslationIndex++;
			String question1 = words.get(currentWordIndexQuiz + 1).split("\\|")[1];
			String question2 = words.get(currentWordIndexQuiz + 2).split("\\|")[1];
			String question3 = words.get(currentWordIndexQuiz + 3).split("\\|")[1];
			String currentWord = words.get(currentWordIndexQuiz).split("\\|")[0];
			String currentAnswer = words.get(currentWordIndexQuiz).split("\\|")[1];
			model.addAttribute("currentWord", currentWord);
			model.addAttribute("currentAnswer", currentAnswer);
			model.addAttribute("question1", question1);
			model.addAttribute("question2", question2);
			model.addAttribute("question3", question3);
			model.addAttribute("currentWordIndex", currentWordIndexQuiz);
		} else {
			model.addAttribute("currentWord", "Koniec");
		}
		return "quiz";
	}
}