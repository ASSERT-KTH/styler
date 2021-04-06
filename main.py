import os
import sys
import configparser
import time
import requests
import json
import itertools
import re
from datetime import datetime, timedelta
import pytz
from github import Github
from github.GithubException import GithubException
from github.GithubException import UnknownObjectException
from github.GithubException import RateLimitExceededException
from collections import OrderedDict
from tqdm import tqdm
import xml.etree.ElementTree as ET
from matplotlib import pyplot as plt
from matplotlib_venn import venn2
from matplotlib_venn import venn3

dir_path = os.path.dirname(os.path.realpath(__file__))
config = configparser.ConfigParser()
config.read(os.path.join(dir_path, "config.ini"))

tokens = []
tokenIndex = -1

checkstyle_file_names = ['checkstyle.xml', '.checkstyle.xml', 'checkstyle_rules.xml', 'checkstyle-rules.xml', 'checkstyle_config.xml', 'checkstyle-config.xml', 'checkstyle_configuration.xml', 'checkstyle-configuration.xml', 'checkstyle_checker.xml', 'checkstyle-checker.xml', 'checkstyle_checks.xml', 'checkstyle-checks.xml', 'google_checks.xml', 'google-checks.xml', 'sun_checks.xml', 'sun-checks.xml']
checkstyle_file_names_search = ""
for checkstyle_file_name in checkstyle_file_names:
    checkstyle_file_names_search += f'filename:{checkstyle_file_name} '
checkstyle_file_names_search.strip()

# checkers that are in Release 8.33 (31.05.2020)
formatting_checkers_release_8_33 = (
    'AnnotationLocation',
    'AnnotationOnSameLine',
    'CommentsIndentation',
    'EmptyForInitializerPad',
    'EmptyForIteratorPad',
    'EmptyLineSeparator',
    'FileTabCharacter',
    'GenericWhitespace',
    'Indentation',
    'LeftCurly',
    'LineLength',
    'MethodParamPad',
    'NewlineAtEndOfFile',
    'NoLineWrap',
    'NoWhitespaceAfter',
    'NoWhitespaceBefore',
    'OneStatementPerLine',
    'OperatorWrap',
    'ParenPad',
    'Regexp',
    'RegexpMultiline',
    'RegexpSingleline',
    'RegexpSinglelineJava',
    'RightCurly',
    'SeparatorWrap',
    'SingleSpaceSeparator',
    'TrailingComment',
    'TypecastParenPad',
    'WhitespaceAfter',
    'WhitespaceAround'
)

# checkers that are in Release 8.33 (31.05.2020)
non_formatting_checkers_release_8_33 = (
    'AbbreviationAsWordInName',
    'AbstractClassName',
    'AnnotationUseStyle',
    'AnonInnerLength',
    'ArrayTrailingComma',
    'ArrayTypeStyle',
    'AtclauseOrder',
    'AvoidDoubleBraceInitialization',
    'AvoidEscapedUnicodeCharacters',
    'AvoidInlineConditionals',
    'AvoidNestedBlocks',
    'AvoidNoArgumentSuperConstructorCall',
    'AvoidStarImport',
    'AvoidStaticImport',
    'BooleanExpressionComplexity',
    'CatchParameterName',
    'ClassDataAbstractionCoupling',
    'ClassFanOutComplexity',
    'ClassMemberImpliedModifier',
    'ClassTypeParameterName',
    'ConstantName',
    'CovariantEquals',
    'CustomImportOrder',
    'CyclomaticComplexity',
    'DeclarationOrder',
    'DefaultComesLast',
    'DescendantToken',
    'DesignForExtension',
    'EmptyBlock',
    'EmptyCatchBlock',
    'EmptyStatement',
    'EqualsAvoidNull',
    'EqualsHashCode',
    'ExecutableStatementCount',
    'ExplicitInitialization',
    'FallThrough',
    'FileLength',
    'FinalClass',
    'FinalLocalVariable',
    'FinalParameters',
    'Header',
    'HiddenField',
    'HideUtilityClassConstructor',
    'IllegalCatch',
    'IllegalImport',
    'IllegalInstantiation',
    'IllegalThrows',
    'IllegalToken',
    'IllegalTokenText',
    'IllegalType',
    'ImportControl',
    'ImportOrder',
    'InnerAssignment',
    'InnerTypeLast',
    'InterfaceIsType',
    'InterfaceMemberImpliedModifier',
    'InterfaceTypeParameterName',
    'InvalidJavadocPosition',
    'JavadocBlockTagLocation',
    'JavadocContentLocation',
    'JavadocMethod',
    'JavadocMissingWhitespaceAfterAsterisk',
    'JavadocPackage',
    'JavadocParagraph',
    'JavadocStyle',
    'JavadocTagContinuationIndentation',
    'JavadocType',
    'JavadocVariable',
    'JavaNCSS',
    'LambdaParameterName',
    'LocalFinalVariableName',
    'LocalVariableName',
    'MagicNumber',
    'MemberName',
    'MethodCount',
    'MethodLength',
    'MethodName',
    'MethodTypeParameterName',
    'MissingCtor',
    'MissingDeprecated',
    'MissingJavadocMethod',
    'MissingJavadocPackage',
    'MissingJavadocType',
    'MissingOverride',
    'MissingSwitchDefault',
    'ModifiedControlVariable',
    'ModifierOrder',
    'MultipleStringLiterals',
    'MultipleVariableDeclarations',
    'MutableException',
    'NeedBraces',
    'NestedForDepth',
    'NestedIfDepth',
    'NestedTryDepth',
    'NoArrayTrailingComma',
    'NoClone',
    'NoCodeInFile',
    'NoEnumTrailingComma',
    'NoFinalizer',
    'NonEmptyAtclauseDescription',
    'NPathComplexity',
    'OneTopLevelClass',
    'OrderedProperties',
    'OuterTypeFilename',
    'OuterTypeNumber',
    'OverloadMethodsDeclarationOrder',
    'PackageAnnotation',
    'PackageDeclaration',
    'PackageName',
    'ParameterAssignment',
    'ParameterName',
    'ParameterNumber',
    'RedundantImport',
    'RedundantModifier',
    'RegexpHeader',
    'RegexpOnFilename',
    'RequireThis',
    'ReturnCount',
    'SimplifyBooleanExpression',
    'SimplifyBooleanReturn',
    'SingleLineJavadoc',
    'StaticVariableName',
    'StringLiteralEquality',
    'SummaryJavadoc',
    'SuperClone',
    'SuperFinalize',
    'SuppressWarnings',
    'SuppressWarningsHolder', # "holder check"
    'ThrowsCount',
    'TodoComment',
    'Translation',
    'TypeName',
    'UncommentedMain',
    'UniqueProperties',
    'UnnecessaryParentheses',
    'UnnecessarySemicolonAfterOuterTypeDeclaration',
    'UnnecessarySemicolonAfterTypeMemberDeclaration',
    'UnnecessarySemicolonInEnumeration',
    'UnnecessarySemicolonInTryWithResources',
    'UnusedImports',
    'UpperEll',
    'VariableDeclarationUsageDistance',
    'VisibilityModifier',
    'WriteTag'
)

# checkers that are *not* in Release 8.33 (31.05.2020)
checkers_before_release_8_33 = (
    'RedundantThrows', # removed in Release 6.2
    'FileContentsHolder', # removed in Release 8.2
    'DoubleCheckedLocking', # removed in Release 5.6
    'GenericIllegalRegexp', # removed in release 4.4 (replaced with RegexpMultiline and RegexpSingleline and RegexpSinglelineJava)
    'TabCharacter', # removed in Release 5.0 Beta 2 (replaced by FileTabCharacter)
    'PackageHtml', # removed in release 4.4 (replaced with JavadocPackage)
    'JUnitTestCase', # removed in Release 6.2
    'StrictDuplicateCode', # removed in Release 6.2
    'FinalStatic', # removed in release 4.4
    'LocalHomeInterface', # removed in release 4.4
    'LocalInterface', # removed in release 4.4
    'MessageBean', # removed in release 4.4
    'RemoteHomeInterface', # removed in release 4.4
    'RemoteInterface', # removed in release 4.4
    'SessionBean', # removed in release 4.4
    'ThisParameter', # removed in release 4.4
    'ThisReturn', # removed in release 4.4
    'EntityBean', # removed in release 4.4
    'RequiredRegexp', # removed in release 4.4 (replaced with RegexpMultiline and RegexpSingleline and RegexpSinglelineJava)
    'NewLineAtEndOfFile', # this word appears in the release notes, it was probably renamed to NewlineAtEndOfFile
    'UnusedLocalVariable' # retired in Release 4.0 Beta 5
)

# filters that are in Release 8.33 (31.05.2020)
filters_release_8_33 = (
    'SeverityMatchFilter',
    'SuppressionCommentFilter',
    'SuppressionFilter',
    'SuppressionSingleFilter',
    'SuppressionXpathFilter',
    'SuppressionXpathSingleFilter',
    'SuppressWarningsFilter',
    'SuppressWithNearbyCommentFilter',
    'SuppressWithPlainTextCommentFilter'
)

# file filters that are in Release 8.33 (31.05.2020)
file_filters_release_8_33 = (
    'BeforeExecutionExclusionFileFilter'
)



def my_print(message):
    print(f'[{datetime.now(pytz.utc).strftime("%Y-%m-%d %H:%M:%S %z")}] {message}', flush=True)

def load_tokens():
    for token in config['DEFAULT']['tokens'].split(','):
        tokens.append(Github(token,per_page=100,timeout=60))
    my_print(f'Got {len(tokens)} GitHub tokens.')

def g():
    global tokenIndex
    if len(tokens) == 1 or (tokenIndex + 1) == len(tokens):
        tokenIndex = 0
    else:
        tokenIndex += 1
    return tokens[tokenIndex]

def sleep(sleep_time):
    my_print(f'Sleep for {sleep_time} seconds...')
    time.sleep(sleep_time)
    my_print('Done waiting - resume.')

def RateLimitExceededException_handler(e, gh):
    my_print(f'[RateLimitExceededException]: {e}')
    delta = gh.get_rate_limit().search.reset - datetime.now()
    sleep_time = delta.seconds % 3600 + 5
    sleep(sleep_time)    

def GithubException_abuse_handler(e):
    my_print(f'[GithubException - ABUSE]: {e}')
    sleep_time = 10
    sleep(sleep_time)

def save_repos(file, repos):
    sorted_repos = sorted(repos, key=str.lower)
    with open(file,'w') as f:
        for repo in sorted_repos:
            if repo.strip(): 
                f.write(repo + '\n')

def iterate_repos(repositories, gh):
    repos = set()
    repos = set(load_repo_list(repos_file))
    nb_repos_before = len(repos)
    count = 0
    _iter = repositories.__iter__()
    run = True
    while run:
        try:
            repo = _iter.__next__()
            count += 1
            repo_id = repo.id
            repo_name = repo.full_name
            repo_line = f'{repo_id},{repo_name}'
            repos.add(repo_line)
            
            with open(repos_raw_file,'a') as f:
                f.write(repo_line + '\n')
        except RateLimitExceededException as e:
            RateLimitExceededException_handler(e, gh)
        except StopIteration:
            save_repos(repos_file, repos)
            nb_repos_after = len(repos)
            nb_addded = nb_repos_after - nb_repos_before
            nb_discarded = count - nb_addded
            my_print(f'Done: Added: {nb_addded} - Discarded (duplicates): {nb_discarded} - Total: {nb_repos_after}')
            run = False

def search_repos_in_dates(q, from_date, to_date, firstCall=False):
    query = f'{q} created:{from_date}..{to_date}'
    my_print(f'Query={query}')
    gh = g()    
    count = 0    
    done = False
    while not done:
        try:
            repositories = gh.search_repositories(query=query)
            if len(repositories.get_page(0)):
                count = repositories.totalCount
            if firstCall:
                my_print(f'Approximate number of repositories to be retrieved: {count}')
            done = True
        except RateLimitExceededException as e:
            RateLimitExceededException_handler(e, gh)
        except Exception as e:
            my_print(e)
    if count < 1000 or from_date == to_date:
        my_print(f'Recording {count} repositories...')
        iterate_repos(repositories, gh)
        return {"from_date": from_date, "to_date": to_date, "count": count}
    else:
        middle = from_date + (to_date - from_date)/2
        return [ search_repos_in_dates(q, from_date, middle), search_repos_in_dates(q, middle + timedelta(days=1), to_date) ]

def download_and_save_file(repo, file, repo_dir):
    my_print(f'Get file ({file}) from {repo.full_name}')
    
    file_name = file.split('/')[-1]
    
    path_to_save = os.path.join(repo_dir, file_name)
    if '/' in file:
        path_to_file = os.path.join(repo_dir, file[:file.rindex('/')])
        if not os.path.exists(path_to_file):        
            os.makedirs(path_to_file)
        path_to_save = os.path.join(repo_dir, path_to_file, file_name)

    contents = repo.get_contents(file)
    request = requests.get(contents.download_url)
    
    with open(path_to_save, 'wb') as f:
        f.write(request.content)

def get_information(repo_id, repo_name):
    my_print(f'Open {repo_name}')
    try:
        repo = g().get_repo(repo_id)
    except UnknownObjectException as e:
        my_print(f'[UnknownObjectException] {e}')
        return 'Not found'
    except Exception as e:
        my_print(f'[Exception] {e}')
        return 'Not found'

    repo_folder_name = f'{repo_id},{repo_name.replace('/',',')}'
    repo_dir = f'{repos_folder_path}/{repo_folder_name}'
    if not os.path.exists(repo_dir):
        os.makedirs(repo_dir)
    repo_info = dict()

    # Gather some information
    repo_info['name'] = repo_name
    repo_info['id'] = repo_id
    repo_info['created_at'] = repo.created_at.strftime("%Y-%m-%d %H:%M:%S")
    repo_info['watchers_count'] = repo.watchers_count
    repo_info['stargazers_count'] = repo.stargazers_count
    repo_info['forks_count'] = repo.forks_count
    repo_info['commits_count'] = repo.get_commits().totalCount
    repo_info['contributors_count'] = repo.get_contributors().totalCount
    repo_info['updated_at'] = repo.updated_at.strftime("%Y-%m-%d %H:%M:%S")
    repo_info['pushed_at'] = repo.pushed_at.strftime("%Y-%m-%d %H:%M:%S")
    repo_info['fetched_at'] = datetime.now(pytz.utc).strftime("%Y-%m-%d %H:%M:%S %z")

    # Get files
    file_paths = []
    file_tree = repo.get_git_tree("HEAD", recursive=True).tree
    for file_obj in file_tree:
        file_paths.append(file_obj.path)
    with open(os.path.join(repo_dir, 'file_paths.json'), 'w') as f:
        json.dump(file_paths, f, indent=4)

    repo_info['checkstyle'] = []
    for file in file_paths:
        #if 'resources' in file:
        #    continue
        cs_file = os.path.basename(file)
        if cs_file in checkstyle_file_names:
            repo_info['checkstyle'].append(file)
            download_and_save_file(repo, file, repo_dir)
    
    with open(os.path.join(repo_dir, info_file_name), 'w') as fp:
        json.dump(repo_info, fp)

    return repo_info

def load_repo_list(path):
    repo_list = []
    if os.path.exists(path):
        with open(path, 'r') as file:
            repo_list = file.read().strip().split('\n')
    return repo_list

def load_downloaded_repo_list(path):
    repo_list = []
    with open(path, 'r') as file:
        repo_list = file.read().split('\n')
    repo_list = [ re.sub('/info.json', '', re.sub('.*/repos/', '', line)) for line in repo_list ]
    return repo_list

def get_checkstyle_file(repo_info, folder):
    selected_file = ''
    for file_path in repo_info['checkstyle']:
        file_name = file_path.split('/')[-1]
        if file_name in checkstyle_file_names:
            if os.path.exists(os.path.join(folder, file_path)):
                if not selected_file:
                    selected_file = file_path
                else:
                    if file_path.count('/') < selected_file.count('/'): # get the file closer to the root
                        selected_file = file_path
    return selected_file

def open_checkstyle(path):
    content = ''
    with open(path, 'r') as f:
        content = f.read()
    content = re.sub("\<\!\-\-.*\-\-\>", "", content, flags=re.MULTILINE)
    parsed_xml = ET.fromstring(content)
    return parsed_xml

def get_checkstyle_modules(cs):
    keep_n_join = lambda a, l: [a] + [ a + '/' + e for e in l ]
    return flatten([ keep_n_join(n.attrib['name'], get_checkstyle_modules(n)) for n in cs if n.tag == 'module' ])

def load_info(folder):
    if len(folder.split('/')) == 2:
        folder = os.path.join(repos_folder_path, folder)
    with open(os.path.join(folder, info_file_name)) as f:
        data = json.load(f)
    return data

def load_file_list(folder):
    file_list = []
    if len(folder.split('/')) == 2:
        folder = os.path.join(repos_folder_path, folder)
    with open(os.path.join(folder, 'file_paths.json')) as f:
        file_list = json.load(f)
    return file_list
    
def has_checkstyle(folder):
    repo_info = load_info(folder)
    for file_path in repo_info['checkstyle']:
        file_name = file_path.split('/')[-1]
        if file_name in checkstyle_file_names:        
            return True
    return False

def has_only_one_checkstyle_file(folder):
    count = 0
    repo_info = load_info(folder)
    for file_path in repo_info['checkstyle']:
        file_name = file_path.split('/')[-1]
        if file_name in checkstyle_file_names:        
            count += 1
    return count == 1

def has_formatting_rule_checker(folder):
    modules_raw, modules_organized = checkstyle_modules_usage([folder])
    if len(modules_organized['formatting_checkers_release_8_33']) > 0:
        return True
    return False

def has_non_formatting_rule_checker(folder):
    modules_raw, modules_organized = checkstyle_modules_usage([folder])
    if len(modules_organized['non_formatting_checkers_release_8_33']) > 0:
        return True
    return False
    
def has_other_rule_checker(folder):
    modules_raw, modules_organized = checkstyle_modules_usage([folder])
    if len(modules_organized['others']) > 0:
        return True
    return False

def has_maven(folder):
    file_list = load_file_list(folder)
    for file_path in file_list:
        file_name = os.path.basename(file_path)
        if file_name == 'pom.xml':
            return True
    return False
    
def has_gradle(folder):
    file_list = load_file_list(folder)
    for file_path in file_list:
        file_name = os.path.basename(file_path)
        if file_name == 'build.gradle':
            return True
    return False
    
def has_ant(folder):
    file_list = load_file_list(folder)
    for file_path in file_list:
        file_name = os.path.basename(file_path)
        if file_name == 'build.xml':
            return True
    return False

def has_travis(folder):
    file_list = load_file_list(folder)
    for file_path in file_list:
        file_name = os.path.basename(file_path)
        if file_name == '.travis.yml':
            return True
    return False
    
def has_circleci(folder):
    file_list = load_file_list(folder)
    for file_path in file_list:
        if file_path.endswith('.circleci/config.yml'):
            return True
    return False

def checkstyle_modules_usage(folders):
    map_modules_to_count = dict()
    for folder in tqdm(folders):
        if len(folder.split('/')) == 2:
            folder = os.path.join(repos_folder_path, folder)
        repo_info = load_info(folder)
        try:
            checkstyle_file = get_checkstyle_file(repo_info, folder)
            if checkstyle_file:
                checkstyle_file_content = open_checkstyle(os.path.join(folder, checkstyle_file))            
                checkstyle_modules = get_checkstyle_modules(checkstyle_file_content)
                for module in checkstyle_modules:
                    map_modules_to_count[module] = map_modules_to_count.get(module, 0) + 1
        except:
            continue

    modules_raw = {}
    modules_organized = {}
    modules_organized['formatting_checkers_release_8_33'] = {}
    modules_organized['non_formatting_checkers_release_8_33'] = {}
    modules_organized['checkers_before_release_8_33'] = {}
    modules_organized['filters_release_8_33'] = {}
    modules_organized['file_filters_release_8_33'] = {}
    modules_organized['others'] = {}
    
    remove_check = lambda x: x if not x.endswith('Check') else x[:-len('Check')]
    for module, count in map_modules_to_count.items():
        if module not in modules_raw:
            modules_raw[module] = 0
        modules_raw[module] += count

        sanitized_module = '/'.join([ remove_check(e.split('.')[-1]) for e in module.split('/') ])
        simple_name = sanitized_module.split('/')[-1].strip()
        
        if simple_name in formatting_checkers_release_8_33:
            if simple_name not in modules_organized['formatting_checkers_release_8_33']:
                modules_organized['formatting_checkers_release_8_33'][simple_name] = 0
            modules_organized['formatting_checkers_release_8_33'][simple_name] += count
        
        if simple_name in non_formatting_checkers_release_8_33:
            if simple_name not in modules_organized['non_formatting_checkers_release_8_33']:
                modules_organized['non_formatting_checkers_release_8_33'][simple_name] = 0
            modules_organized['non_formatting_checkers_release_8_33'][simple_name] += count

        if simple_name in checkers_before_release_8_33:
            if simple_name not in modules_organized['checkers_before_release_8_33']:
                modules_organized['checkers_before_release_8_33'][simple_name] = 0
            modules_organized['checkers_before_release_8_33'][simple_name] += count

        if simple_name in filters_release_8_33:
            if simple_name not in modules_organized['filters_release_8_33']:
                modules_organized['filters_release_8_33'][simple_name] = 0
            modules_organized['filters_release_8_33'][simple_name] += count

        if simple_name in file_filters_release_8_33:
            if simple_name not in modules_organized['file_filters_release_8_33']:
                modules_organized['file_filters_release_8_33'][simple_name] = 0
            modules_organized['file_filters_release_8_33'][simple_name] += count
        
        if (simple_name not in formatting_checkers_release_8_33) and (simple_name not in non_formatting_checkers_release_8_33) and (simple_name not in checkers_before_release_8_33) and (simple_name not in filters_release_8_33) and (simple_name not in file_filters_release_8_33):
            if simple_name not in modules_organized['others']:
                modules_organized['others'][simple_name] = 0
            modules_organized['others'][simple_name] += count

    return modules_raw, modules_organized

def load_folders(file):
    dirs = []
    with open(file, 'r') as f:
        dirs = f.read().split('\n')
    dirs = [ '/'.join(dir.split('/')[:-1]) for dir in dirs if dir != '']
    return dirs

def flatten(l):
    if len(l) == 0:
        return []
    return flatten(l[0]) + (flatten(l[1:]) if len(l) > 1 else []) if type(l) is list else [l]

def findsubsets(S):
    return flatten([ list(itertools.combinations(S,r)) for r in range(0, len(S) + 1) ])

def filters(filters_list, list_to_be_filtered):
    if len(filters_list) == 0:
        return list_to_be_filtered
    else:
        filter_to_apply = filters_list.pop()
        return filters(filters_list, filter(filter_to_apply, list_to_be_filtered))

def venn(sets, repos):
    groups = findsubsets(sets.keys())
    result = dict()
    for group in tqdm(sorted(groups, key=lambda k: len(k), reverse=True), desc='Compute the venn'):
        group_filters = [ sets[key] for key in group ]
        if len(group) == len(sets):
            result[group] = len(list(filters(group_filters, repos)))
        else:
            tmp_group = len(list(filters(group_filters, repos)))
            for past_group in result.keys():
                if set(group).issubset(set(past_group)):
                    tmp_group -= result[past_group]
            result[group] = tmp_group
    
    return result

def save_json(file_name, content, sort=False):
    with open(file_name, 'w') as f:
        json.dump(content, f, indent=4, sort_keys=sort)
        

result_folder_path = os.path.join(dir_path, 'results')
if not os.path.exists(result_folder_path):
    os.mkdir(result_folder_path)
repos_file = os.path.join(result_folder_path, 'repos.txt')
repos_raw_file = os.path.join(result_folder_path, 'repos_raw.txt')
repos_folder_path = os.path.join(result_folder_path, 'repos')
if not os.path.exists(repos_folder_path):
    os.mkdir(repos_folder_path)
download_file = os.path.join(result_folder_path, 'downloaded.txt')
info_file_name = 'info.json'
repos_with_checkstyle_file_file = os.path.join(result_folder_path, 'repos_with_checkstyle_file.txt')
repos_with_only_one_checkstyle_file_formatting_rule_checker_and_maven_file = os.path.join(result_folder_path, 'repos_with_only_one_checkstyle_file_formatting_rule_checker_and_maven.txt')
modules_raw_file = os.path.join(result_folder_path, 'modules_raw.json')
modules_organized_file = os.path.join(result_folder_path, 'modules_organized.json')

if __name__ == '__main__':
    my_print(f'Beginning...')

    if sys.argv[1] == 'search-repos':
        load_tokens()

        try:        
            from_date = datetime.strptime(config['DEFAULT']['from_date'],"%Y/%m/%d").date()
            to_date = datetime.strptime(config['DEFAULT']['to_date'],"%Y/%m/%d").date()
        except:
            my_print('Provide dates (from_date and to_date) in the config.ini file following the format YYYY/MM/DD.')
            sys.exit()

        query_general_search = config['DEFAULT']['query_general_search']
        if query_general_search:
            query = f'{query_general_search} created:{from_date}..{to_date}'
            my_print(f'Query={query}')
            gh = g()    
            done = False
            while not done:
                try:
                    repositories = gh.search_repositories(query=query)
                    if len(repositories.get_page(0)):
                        count = repositories.totalCount
                    my_print(f'Approximate number of repositories: {count}')
                    done = True
                except RateLimitExceededException as e:
                    RateLimitExceededException_handler(e, gh)

        query_detailed_search = config['DEFAULT']['query_detailed_search']
        if query_detailed_search:
            search_repos_in_dates(query_detailed_search, from_date, to_date, firstCall=True)
        else:
            my_print('Provide a query for detailed search (query_detailed_search) in the config.ini file.')
        my_print(f'End.')
        sys.exit()

    os.popen(f'find {repos_folder_path} -name \'{info_file_name}\' > {download_file}').read()
    if sys.argv[1] == 'get-repos-info':        
        load_tokens()

        if len(sys.argv) == 3:
            repos_file = sys.argv[2]

        repo_list = set(load_repo_list(repos_file)) - set(load_downloaded_repo_list(download_file))
        my_print(f'{len(repo_list)} repos to fetch...')
        for repo in tqdm(repo_list, desc='Fetch repos'):
            repo_id = int(repo.split(',')[0])
            repo_name = repo.split(',')[1]
            done = False
            while not done:
                try:
                    get_information(repo_id, repo_name)
                    done = True
                except RateLimitExceededException as e:
                    RateLimitExceededException_handler(e, tokens[tokenIndex])
                except GithubException as e:
                    if e.status == 403: # abuse-rate-limits
                        GithubException_abuse_handler(e)
                    else:
                        my_print(f'[GithubException]: {e}')
                        done = True
                except Exception as e:
                    my_print(e)
                    done = True
        my_print(f'End.')
        sys.exit()

    # get repos using checkstyle
    repos = load_folders(download_file)
    filtered_repos = map(lambda folder: re.sub(".*/repos/", "", folder), filters([has_checkstyle], repos))
    save_repos(repos_with_checkstyle_file_file, sorted(filtered_repos, key=str.lower))

    if sys.argv[1] == 'checkstyle-modules-usage':
        repos = load_repo_list(repos_with_checkstyle_file_file)
        modules_raw, modules_organized = checkstyle_modules_usage(repos)
        save_json(modules_raw_file, OrderedDict(sorted(modules_raw.items(), key=lambda k: k[1], reverse=True)))
        sorted_modules_organized = {}
        sorted_modules_organized['formatting_checkers_release_8_33'] = OrderedDict(sorted(modules_organized['formatting_checkers_release_8_33'].items(), key=lambda k: k[1], reverse=True))
        sorted_modules_organized['non_formatting_checkers_release_8_33'] = OrderedDict(sorted(modules_organized['non_formatting_checkers_release_8_33'].items(), key=lambda k: k[1], reverse=True))
        sorted_modules_organized['checkers_before_release_8_33'] = OrderedDict(sorted(modules_organized['checkers_before_release_8_33'].items(), key=lambda k: k[1], reverse=True))
        sorted_modules_organized['checkstyle_filters_release_8_33'] = OrderedDict(sorted(modules_organized['filters_release_8_33'].items(), key=lambda k: k[1], reverse=True))
        sorted_modules_organized['checkstyle_file_filters_release_8_33'] = OrderedDict(sorted(modules_organized['file_filters_release_8_33'].items(), key=lambda k: k[1], reverse=True))
        sorted_modules_organized['others'] = OrderedDict(sorted(modules_organized['others'].items(), key=lambda k: k[1], reverse=True))
        save_json(modules_organized_file, sorted_modules_organized)

    if sys.argv[1] == 'venn':
        repos = load_repo_list(repos_with_checkstyle_file_file)
        
        sets = {'maven': has_maven, 'gradle': has_gradle, 'ant': has_ant}
        result = venn(sets, repos)
        sets_values = []
        sets_values.append(result[('maven',)])
        sets_values.append(result[('gradle',)])
        sets_values.append(result[('maven', 'gradle')])
        sets_values.append(result[('ant',)])
        sets_values.append(result[('maven', 'ant')])
        sets_values.append(result[('gradle', 'ant')])
        sets_values.append(result[('maven', 'gradle', 'ant')])
        sets_labels = ('maven', 'gradle', 'ant')
        venn3(subsets = sets_values, set_labels = sets_labels)
        #plt.show()
        plt.savefig(os.path.join(result_folder_path, 'venn_build_tool.pdf'))
        plt.close()
        
        sets = {'travis': has_travis, 'circleci': has_circleci}
        result = venn(sets, repos)
        sets_values = []
        sets_values.append(result[('travis',)])
        sets_values.append(result[('circleci',)])
        sets_values.append(result[('travis', 'circleci')])
        sets_labels = ('travis', 'circleci')
        venn2(subsets = sets_values, set_labels = sets_labels)
        #plt.show()
        plt.savefig(os.path.join(result_folder_path, 'venn_ci.pdf'))
        plt.close()
        
        sets = {'Formatting rules': has_formatting_rule_checker, 'Non-formatting rules': has_non_formatting_rule_checker, 'Other rules': has_other_rule_checker}
        result = venn(sets, repos)
        sets_values = []
        sets_values.append(result[('Formatting rules',)])
        sets_values.append(result[('Non-formatting rules',)])
        sets_values.append(result[('Formatting rules', 'Non-formatting rules')])
        sets_values.append(result[('Other rules',)])
        sets_values.append(result[('Formatting rules', 'Other rules')])
        sets_values.append(result[('Non-formatting rules', 'Other rules')])
        sets_values.append(result[('Formatting rules', 'Non-formatting rules', 'Other rules')])
        sets_labels = ('Formatting rules', 'Non-formatting rules', 'Other rules')
        venn3(subsets = sets_values, set_labels = sets_labels)
        #plt.show()
        plt.savefig(os.path.join(result_folder_path, 'venn_rules.pdf'))

    if sys.argv[1] == 'list-repos-with-only-one-checkstyle-file-formatting-rule-checker-and-maven':
        repos = load_repo_list(repos_with_checkstyle_file_file)
        filtered_repos = map(lambda folder: re.sub(".*/repos/", "", folder), filters([has_only_one_checkstyle_file, has_formatting_rule_checker, has_maven], repos))
        save_repos(repos_with_only_one_checkstyle_file_formatting_rule_checker_and_maven_file, sorted(filtered_repos, key=str.lower))

    my_print(f'End.')
