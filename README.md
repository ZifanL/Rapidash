# Rapidash
The repository contains the source code for [Rapidash](https://www.vldb.org/pvldb/vol17/p2009-deep.pdf), an efficient system to detect violations to denial constraints.

## Requirements

To run this project, ensure that you have the following installed:

- **Java 17 or above**: The project is built and tested with Java 17. You can download the latest version of Java from the [Oracle website](https://www.oracle.com/java/technologies/javase-downloads.html) or [OpenJDK](https://jdk.java.net/).

- **Maven**: Maven is used for dependency management and building the project. You can download Maven from the [Maven website](https://maven.apache.org/download.cgi).

## Installation

1. **Verify Java Installation**: Ensure that Java 17 or above is installed by running the following command in your terminal:
    ```sh
    java -version
    ```
    The output should display a Java version of 17 or above.

2. **Verify Maven Installation**: Ensure that Maven is installed by running the following command in your terminal:
    ```sh
    mvn -version
    ```
    The output should display the Maven version.

## Build the Project

1. **Clone the Repository**: Clone the project repository to your local machine:
    ```sh
    git https://github.com/ZifanL/Rapidash.git
    cd Rapidash
    ```

2. **Build the Project**: Use Maven to build the project:
    ```sh
    mvn clean install
    ```

## Run Rapidash
### Input File Format
- The input dataset should be a csv file.
- Write the constraint to be verified in a file. Each line should be in the form of `[column-A] [operator] [column-B]` that represents predicate `s.[column-A] [operator] t.[column-B]`, where `[column-A]` and `[column-B]` are column names (they can be the same or be different), `s` and `t` are two different rows, and `[operator]` should be one of `==, <>, >, >=, <, <=`. For example, to verify `NOT (s.Category == t.Category AND s.ID <= t.Amount)`, we write the following to `dc.txt`:
    ```
    Category = Category
    ID <= Amount
    ```
### How to Run
```sh
java -cp target/rapidash-1.0-SNAPSHOT-jar-with-dependencies.jar org.dc.Main --dataset [path-to-csv-file] --constraint [path-to-constraint-file] --earlystop [earlystop] --treetype [treetype]
```
- `[path-to-csv-file]` is the path to the input csv file.
- `[path-to-constraint-file]` is the path to the file that contains the denial constraint.
- `[earlystop]` is either `true` or `false`. If it is set to `true`, the system will stop when the first violation is found. If it is set to `false`, the system will output the count of the violations. The default value is `true`
- `[treetype]` is either `range-tree` or `kd-tree`, which specifies which data structure to use. Refer to the [paper](https://www.vldb.org/pvldb/vol17/p2009-deep.pdf) for the comparison between the two. `range-tree` is used by default.

### Example
We run Rapidash use a toy dataset:
```sh
java -cp target/rapidash-1.0-SNAPSHOT-jar-with-dependencies.jar org.dc.Main --dataset data/toy.csv --constraint data/dc.txt
```

## Reproduce the Experimental Results
Here are the steps to reproduce the experimental results in the [paper](https://www.vldb.org/pvldb/vol17/p2009-deep.pdf):
### Download the Data
Download the [data](https://www.dropbox.com/scl/fi/azxswgry8jjk23o92vsy0/rapidash_data.zip?rlkey=rvosjfu30dzznyrki824bghy0&st=ijqiiov0&dl=0) and uncompress. Note that the values in the datasets are encoded as integers, and the order is preserved for numerical values.
### Run the Experiments
   ```sh
    java -cp target/rapidash-1.0-SNAPSHOT-jar-with-dependencies.jar org.dc.Main --experiment [experiment-name]
   ```
where [experiment-name] should be one of "tax", "tpch" and "ncvoter"

## Citation
Please cite our paper if you find this repo helpful in your work:
```
@article{liu2024rapidash,
  title={Rapidash: Efficient Detection of Constraint Violations},
  author={Liu, Zifan and Deep, Shaleen and Fariha, Anna and Psallidas, Fotis and Tiwari, Ashish and Floratou, Avrilia},
  journal={Proceedings of the VLDB Endowment},
  volume={17},
  number={8},
  pages={2009--2021},
  year={2024},
  publisher={VLDB Endowment}
}
```