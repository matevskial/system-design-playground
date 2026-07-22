const prompt = "Enter a your name: ";
process.stdout.write(prompt);

let state = "INPUT_NAME";
for await (const line of console) {
  if (state === "INPUT_NAME") {
    state = "INPUT_ANSWER";
    console.log(`Hello ${line}`);
    process.stdout.write("Answer this with yes or no: Do you support Aleksandar for president? ");
  } else if (state === "INPUT_ANSWER") {
    const answeredYes = line.trim().toLowerCase().indexOf("yes") != -1;
    if (answeredYes) {
      state = "END_YES";
      process.stdout.write("Thank you, have a nice day. ");
      break;
    } else {
      state = "INPUT_ANSWER";
      process.stdout.write("Wrong answer, try again, do you fucking support Aleksandar for president? ");
    }
  }
}